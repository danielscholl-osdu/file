package org.opengroup.osdu.file.constants;

public class TestConstants {
	public static final String FORWARD_SLASH = "/";
	public static final String PUT_ENDPOINT = "";
	public static final String STORAGE_HOST = System.getProperty("STORAGE_HOST",
			System.getenv("STORAGE_HOST"));
	public static final String STORAGE_GET_ENDPOINT = "/records/";
	public static final String POST_ENDPOINT = "/v2/files/metadata";
	public static final String GET_SIGNEDURL_UPLOAD_ENDPOINT = "/v2/files/uploadURL";
	public static final String GET_SIGNEDURL_DOWNLOAD_ENDPOINT1 = "/v2/files/";
	public static final String GET_SIGNEDURL_DOWNLOAD_ENDPOINT2 = "/downloadURL";
	public static final String GET_METADATA_ENDPOINT2 = "/metadata";
  public static final String DMS_GET_STORAGE_INSTRUCTIONS_ENDPOINT = "/v2/files/storageInstructions";
  public static final String DMS_GET_RETRIEVAL_INSTRUCTIONS_ENDPOINT ="/v2/files/retrievalInstructions";
	public static final String GET_ENDPOINT = "";
	public static final String GET_FLATTENED_ENDPOINT = "";
	public static final String INTERNAL_SERVER_ERROR = "internal server error";
	public static final String FILE_SERVICE_KIND = "";
	public static final String GET_SUCCESSRESPONSECODE = "200";
	public static final String DATAECOSYSTEM = "dataecosystem";
	public static final String AUTHORIZATION = "authorization";
	public static final String DATA_PARTITION_ID = "data-partition-id";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String JSON_CONTENT = "application/json";
	public static final String DOT = ".";
	public static final String ID = "id";
	public static final String ERROR = "error";
	public static final String MESSAGE = "message";
	public static final String USER = "user";
	public static final String HOST = System.getProperty("FILE_SERVICE_HOST", System.getenv("FILE_SERVICE_HOST"));
	public static final String PRIVATE_TENANT1 = System.getProperty("PRIVATE_TENANT1",
			System.getenv("PRIVATE_TENANT1"));
	public static final String PRIVATE_TENANT2 = System.getProperty("PRIVATE_TENANT2",
			System.getenv("PRIVATE_TENANT2"));
	public static final String SHARED_TENANT = System.getProperty("SHARED_TENANT", System.getenv("SHARED_TENANT"));
	public static final String SIGNED_URL = "SignedUrl";
	public static final String FILE_SOURCE = "FileSource";
	public static final String TENANT_NAME_PLACEHOLDER = "<tenant_name>";
	public static final String ACL_VIEWERS_GROUP = "<acl_viewers>";
	public static final String ACL_OWNERS_GROUP = "<acl_owners>";
	public static final String CLOUD_DOMAIN = "<cloud_domain>";
	public static final String LEGAL_TAGS = "<legal_tags>";
  public static final String REGISTRY_ID = "<registry_id>";
	public static final String TENANT_NAME_PLACEHOLDER_VALUE = System.getProperty("TENANT_NAME",
			System.getenv("TENANT_NAME"));
	public static final String ACL_VIEWERS_GROUP_VALUE = System.getProperty("ACL_VIEWERS",
			System.getenv("ACL_VIEWERS"));
	public static final String ACL_OWNERS_GROUP_VALUE = System.getProperty("ACL_OWNERS", System.getenv("ACL_OWNERS"));
	public static final String CLOUD_DOMAIN_VALUE = System.getProperty("DOMAIN", System.getenv("DOMAIN"));
	public static final String LEGAL_TAGS_VALUE = System.getProperty("LEGAL_TAG", System.getenv("LEGAL_TAG"));

	public static final String GET_LOCATION = "/v2/getLocation";
	public static final String GET_FILE_LOCATION = "/v2/getFileLocation";
	public static final String GET_FILE_LIST = "/v2/getFileList";
	public static final String EXPIRY_TIME_PARA_NAME = "expiryTime";
}
