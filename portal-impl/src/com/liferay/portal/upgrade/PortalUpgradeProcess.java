/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeVersionTreeMap;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.upgrade.util.PortalUpgradeProcessRegistry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

/**
 * @author Alberto Chaparro
 */
public class PortalUpgradeProcess extends UpgradeProcess {

	public static Version getCurrentSchemaVersion(Connection connection)
		throws SQLException {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select schemaVersion from Release_ where servletContextName " +
					"= ?")) {

			preparedStatement.setString(
				1, ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String schemaVersion = resultSet.getString("schemaVersion");

					return Version.parseVersion(schemaVersion);
				}
			}
		}

		return new Version(0, 0, 0);
	}

	public static Version getLatestSchemaVersion() {
		return _upgradeVersionTreeMap.lastKey();
	}

	public static SortedMap<Version, UpgradeProcess> getPendingUpgradeProcesses(
		Version schemaVersion) {

		return _upgradeVersionTreeMap.tailMap(schemaVersion, false);
	}

	public static Version getRequiredSchemaVersion() {
		NavigableSet<Version> reverseSchemaVersions =
			_upgradeVersionTreeMap.descendingKeySet();

		Iterator<Version> iterator = reverseSchemaVersions.iterator();

		Version requiredSchemaVersion = iterator.next();

		while (iterator.hasNext()) {
			Version nextSchemaVersion = iterator.next();

			if ((requiredSchemaVersion.getMajor() !=
					nextSchemaVersion.getMajor()) ||
				(requiredSchemaVersion.getMinor() !=
					nextSchemaVersion.getMinor())) {

				break;
			}

			if (requiredSchemaVersion.getMicro() !=
					nextSchemaVersion.getMicro()) {

				requiredSchemaVersion = nextSchemaVersion;
			}
		}

		return requiredSchemaVersion;
	}

	public static boolean isInLatestSchemaVersion(Connection connection)
		throws SQLException {

		Version latestSchemaVersion = getLatestSchemaVersion();

		if (latestSchemaVersion.equals(getCurrentSchemaVersion(connection))) {
			return true;
		}

		return false;
	}

	public static boolean isInRequiredSchemaVersion(Connection connection)
		throws SQLException {

		Version currentSchemaVersion = getCurrentSchemaVersion(connection);

		Version requiredSchemaVersion = getRequiredSchemaVersion();

		int result = requiredSchemaVersion.compareTo(currentSchemaVersion);

		if ((result == 0) ||
			((result < 0) &&
			 (requiredSchemaVersion.getMajor() ==
				 currentSchemaVersion.getMajor()))) {

			return true;
		}

		return false;
	}

	public static boolean supportsRetry(Connection connection)
		throws SQLException {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select buildNumber from Release_ where servletContextName = " +
					"?")) {

			preparedStatement.setString(
				1, ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					if (resultSet.getInt("buildNumber") >=
							ReleaseInfo.RELEASE_7_1_0_BUILD_NUMBER) {

						return true;
					}

					return false;
				}
			}
		}

		return false;
	}

	@Override
	public void upgrade() throws UpgradeException {
		long start = System.currentTimeMillis();

		String message = "Completed upgrade process ";

		try (Connection connection = getConnection()) {
			this.connection = connection;

			if (_log.isInfoEnabled()) {
				String info = "Upgrading " + ClassUtil.getClassName(this);

				_log.info(info);
			}

			doUpgrade();
		}
		catch (Exception exception) {
			message = "Failed upgrade process ";

			throw new UpgradeException(exception);
		}
		finally {
			this.connection = null;

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						message, ClassUtil.getClassName(this), " in ",
						System.currentTimeMillis() - start, " ms"));
			}
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		_initializeRelease(connection);

		for (Version pendingSchemaVersion :
				getPendingSchemaVersions(getCurrentSchemaVersion(connection))) {

			upgrade(_upgradeVersionTreeMap.get(pendingSchemaVersion));

			updateSchemaVersion(pendingSchemaVersion);
		}

		clearIndexesCache();
	}

	protected Set<Version> getPendingSchemaVersions(Version fromSchemaVersion) {
		SortedMap<Version, UpgradeProcess> pendingUpgradeProcesses =
			_upgradeVersionTreeMap.tailMap(fromSchemaVersion, false);

		return pendingUpgradeProcesses.keySet();
	}

	protected void updateSchemaVersion(Version newSchemaVersion)
		throws SQLException {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update Release_ set schemaVersion = ? where " +
					"servletContextName = ?")) {

			preparedStatement.setString(1, newSchemaVersion.toString());
			preparedStatement.setString(
				2, ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);

			preparedStatement.execute();
		}
	}

	private static void _registerUpgradeProcesses(
		PortalUpgradeProcessRegistry... portalUpgradeProcessRegistries) {

		for (PortalUpgradeProcessRegistry portalUpgradeProcessRegistry :
				portalUpgradeProcessRegistries) {

			portalUpgradeProcessRegistry.registerUpgradeProcesses(
				_upgradeVersionTreeMap);
		}
	}

	private void _initializeRelease(Connection connection) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update Release_ set schemaVersion = ?, buildNumber = ? " +
					"where servletContextName = ? and buildNumber < ?")) {

			preparedStatement.setString(1, _initialSchemaVersion.toString());
			preparedStatement.setInt(2, ReleaseInfo.RELEASE_7_1_0_BUILD_NUMBER);
			preparedStatement.setString(
				3, ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);
			preparedStatement.setInt(4, ReleaseInfo.RELEASE_7_1_0_BUILD_NUMBER);

			preparedStatement.execute();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalUpgradeProcess.class);

	private static final Version _initialSchemaVersion = new Version(0, 1, 0);
	private static final UpgradeVersionTreeMap _upgradeVersionTreeMap =
		new UpgradeVersionTreeMap() {
			{
				put(_initialSchemaVersion, new DummyUpgradeProcess());
			}
		};

	static {
		_registerUpgradeProcesses(
			new com.liferay.portal.upgrade.v7_1_x.
				PortalUpgradeProcessRegistryImpl(),
			new com.liferay.portal.upgrade.v7_2_x.
				PortalUpgradeProcessRegistryImpl(),
			new com.liferay.portal.upgrade.v7_3_x.
				PortalUpgradeProcessRegistryImpl(),
			new com.liferay.portal.upgrade.v7_4_x.
				PortalUpgradeProcessRegistryImpl());
	}

}