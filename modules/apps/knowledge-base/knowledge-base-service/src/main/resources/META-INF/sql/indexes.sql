create index IX_A73BDBFC on KBArticle (companyId, latest, ctCollectionId);
create index IX_CBFFCEEE on KBArticle (companyId, main, ctCollectionId);
create index IX_CF97F7A7 on KBArticle (companyId, status, ctCollectionId);
create index IX_CB41FF56 on KBArticle (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_7AF9AA6E on KBArticle (groupId, externalReferenceCode[$COLUMN_LENGTH:75$], version, ctCollectionId);
create index IX_46C964D2 on KBArticle (groupId, kbFolderId, latest, ctCollectionId);
create index IX_6F25807D on KBArticle (groupId, kbFolderId, status, ctCollectionId);
create index IX_A27A3834 on KBArticle (groupId, kbFolderId, urlTitle[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_EB5DB51A on KBArticle (groupId, kbFolderId, urlTitle[$COLUMN_LENGTH:75$], status, ctCollectionId);
create index IX_B5D7B93E on KBArticle (groupId, latest, ctCollectionId);
create index IX_471614B0 on KBArticle (groupId, main, ctCollectionId);
create index IX_42AE23A5 on KBArticle (groupId, parentResourcePrimKey, latest, ctCollectionId);
create index IX_ECAB9F8B on KBArticle (groupId, parentResourcePrimKey, latest, status, ctCollectionId);
create index IX_3F81ECD7 on KBArticle (groupId, parentResourcePrimKey, main, ctCollectionId);
create index IX_6B0A3F50 on KBArticle (groupId, parentResourcePrimKey, status, ctCollectionId);
create index IX_DE33D4E9 on KBArticle (groupId, status, ctCollectionId);
create index IX_F49D9AA5 on KBArticle (parentResourcePrimKey, latest, ctCollectionId);
create index IX_C4E323D7 on KBArticle (parentResourcePrimKey, main, ctCollectionId);
create index IX_1CF9B650 on KBArticle (parentResourcePrimKey, status, ctCollectionId);
create index IX_7A4A79B4 on KBArticle (resourcePrimKey, ctCollectionId);
create index IX_11097472 on KBArticle (resourcePrimKey, groupId, ctCollectionId);
create index IX_2527B7AD on KBArticle (resourcePrimKey, groupId, latest, ctCollectionId);
create index IX_3DE0E2DF on KBArticle (resourcePrimKey, groupId, main, ctCollectionId);
create index IX_4D83D358 on KBArticle (resourcePrimKey, groupId, status, ctCollectionId);
create index IX_4D385AEF on KBArticle (resourcePrimKey, latest, ctCollectionId);
create index IX_14E56EA1 on KBArticle (resourcePrimKey, main, ctCollectionId);
create index IX_7594769A on KBArticle (resourcePrimKey, status, ctCollectionId);
create unique index IX_E7D1F9D0 on KBArticle (resourcePrimKey, version, ctCollectionId);
create index IX_94DAB7 on KBArticle (uuid_[$COLUMN_LENGTH:75$], companyId, ctCollectionId);
create index IX_71CDBACD on KBArticle (uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_5DCB5F79 on KBArticle (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_3B3F301 on KBComment (classNameId, classPK, ctCollectionId);
create index IX_770A52E7 on KBComment (classNameId, classPK, status, ctCollectionId);
create index IX_E9DA6B90 on KBComment (groupId, classNameId, ctCollectionId);
create index IX_3A7ADBFA on KBComment (groupId, ctCollectionId);
create index IX_C2F932E0 on KBComment (groupId, status, ctCollectionId);
create index IX_F08D21BB on KBComment (userId, classNameId, classPK, ctCollectionId);
create index IX_B47B3BA0 on KBComment (uuid_[$COLUMN_LENGTH:75$], companyId, ctCollectionId);
create index IX_53FAD184 on KBComment (uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_8E76E6A2 on KBComment (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_66C3047B on KBFolder (companyId, ctCollectionId);
create unique index IX_698CF996 on KBFolder (externalReferenceCode[$COLUMN_LENGTH:75$], groupId, ctCollectionId);
create index IX_95BD27BB on KBFolder (groupId, parentKBFolderId, ctCollectionId);
create index IX_2B78DFBA on KBFolder (groupId, parentKBFolderId, name[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_41B62C58 on KBFolder (groupId, parentKBFolderId, urlTitle[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_932548BD on KBFolder (uuid_[$COLUMN_LENGTH:75$], companyId, ctCollectionId);
create index IX_D675D487 on KBFolder (uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_5F8B96FF on KBFolder (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);

create index IX_A0C81C71 on KBTemplate (groupId, ctCollectionId);
create index IX_FF399109 on KBTemplate (uuid_[$COLUMN_LENGTH:75$], companyId, ctCollectionId);
create index IX_66BBC3BB on KBTemplate (uuid_[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_7C6D824B on KBTemplate (uuid_[$COLUMN_LENGTH:75$], groupId, ctCollectionId);