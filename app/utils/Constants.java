package utils;

public class Constants {

	public static final String URL_HOST = "http://localhost";
	public static final String URL_SERVER = "http://einstein.sv.cmu.edu";
	//public static final String URL_SERVER = "http://localhost";


	// port
	public static final String JPL_BACKEND_PORT = ":9002";
	public static final String LOCAL_HOST_PORT = ":9032";
	public static final String CMU_BACKEND_PORT = ":9035";
	public static final String URL_FLASK = ":9040";

	// API Call format
	public static final String FORMAT = "json";

	// add all parameter
	public static final String ADD_ALL_PARAMETERS = "/parameter/addParameter";

	// climate service
	public static final String ADD_CLIMATE_SERVICE = "/climateService/addClimateService";
	public static final String GET_ALL_CLIMATE_SERVICES = "/climateService/getAllClimateServices/json";
	public static final String GET_MOST_RECENTLY_ADDED_CLIMATE_SERVICES_CALL = "/climateService/getAllMostRecentClimateServicesByCreateTime/json";
	public static final String GET_MOST_POPULAR_CLIMATE_SERVICES_CALL = "/climateService/getAllMostUsedClimateServices/json";

	public static final String GET_MOST_RECENTLY_USED_CLIMATE_SERVICES_CALL = "/climateService/getAllMostRecentClimateServicesByLatestAccessTime/json";
	public static final String GET_CLIMATE_SERVICES_CALL = "/climateService/getAllClimateServices/json";
	public static final String GET_TOP_K_USED_CLIMATE_SERVICES_BY_DATASET_ID = "/climateService/getTopKUsedClimateServicesByDatasetId";

	// climate service page
	public static final String SAVE_CLIMATE_SERVICE_PAGE = "/climateService/savePage";
	public static final String QUERY_CLIMATE_SERVICE = "/climateService/queryClimateService";

	// user
	public static final String IS_USER_VALID = "/users/isUserValid";
	public static final String ADD_USER = "/users/add";
	public static final String IS_EMAIL_EXISTED = "/users/isEmailExisted";

	//climate service execution log
	public static final String GET_ALL_SERVICE_LOG = "/serviceExecutionLog/getAllServiceExecutionLog";
	public static final String QUERY_SERVICE_LOG = "/serviceExecutionLog/queryServiceExecutionLogs";

	// dataset
	public static final String GET_ALL_DATASETS = "/dataset/getAllDatasets/json";
	public static final String DATASET_QUERY = "/dataset/queryDataset";
	public static final String GET_MOST_K_POPULAR_DATASETS_CALL = "/dataset/getMostKPopularDatasets";

	// dataset log
	public static final String GET_ALL_DATASETLOGS = "/datasetLog/getAllDatasetLogs/json";

	// users
	public static final String GET_ALL_USERS = "/users/getAllUsers/json";
	public static final String GET_USER_BY_EMAIL = "/users/getUserByEmail";
	public static final String GET_TOP_K_SIMILAR_USERS = "/recommendation/getTopKSimilarUsersById/";


	// bug report
	public static final String ADD_BUG_REPORT = "/bugReport/addBugReport";
	public static final String GET_ALL_BUG_REPORTS = "/bugReport/getAllBugReports/json";
	public static final String DELETE_ONE_BUG_REPORT = "/bugReport/deleteBugReport/id/";
	public static final String UPDATE_BUG_REPORT = "/bugReport/updateBugReport/id/";
	public static final String GET_BUG_REPORT_BY_ID = "/bugReport/getBugReport/id/";

	// http://www.freeformatter.com/java-dotnet-escape.html -- this is for escape text purpose
	// html head

	public static final String htmlHead1 = "<head>\r\n    <meta charset=\"utf-8\">\r\n    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->\r\n    <title>Climate Service</title>\r\n    \r\n    <script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-2.1.4.min.js\"></script>\r\n    <script type=\"text/javascript\" src=\"/assets/javascripts/parameter.js\"></script>\r\n\r\n   ";
	public static final String htmlHead2	=	" </script><!-- Bootstrap -->\r\n    <link href=\"/assets/stylesheets/bootstrap.min.css\" rel=\"stylesheet\">\r\n\r\n    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->\r\n    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->\r\n    <!--[if lt IE 9]>\r\n    <script src=\"https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js\"></script>\r\n    <script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script>\r\n    <![endif]-->\r\n</head>\r\n<body>\r\n\r\n<h2 class=\"text-center\">";
	public static final String htmlHead3 = "</h2>\r\n\r\n<p class=\"text-center col-md-8 col-md-offset-2\">";
	public static final String htmlHead4 = "</p>\r\n\r\n<div class=\"container col-md-6\">\r\n    <form>\r\n        <table class=\"table table-bordered table-striped\">\r\n            <thead>\r\n            <tr>\r\n                <th class=\"col-md-2\">Parameter Name</th>\r\n                <th class=\"col-md-4\">Value</th>\r\n            </tr>\r\n            </thead>\r\n            <tbody id=\"dynamicTBody\">";
	public static final String putVar = "var varList = {\r\n\"pr\":       [\"Precipitation Flux\",                                \"\", 2, \"\"],    \r\n\"clt\":      [\"Total Cloud Fraction\",                              \"\", 2, \"\"],      \r\n\"ts\":       [\"Surface Temperature\",                               \"\", 2, \"\"],     \r\n\"lst_day\":  [\"Daytime Land Surface Temperature\",                  \"\", 2, \"\"],   \r\n\"lst_night\":[\"Nighttime Land Surface Temperature\",                \"\", 2, \"\"],   \r\n\"tas\":      [\"Near-Surface Air Temperature\",                      \"\", 2, \"\"],   \r\n\"hurs\":     [\"Near-Surface Relative Humidity\",                    \"\", 2, \"\"],   \r\n\"tos\":      [\"Sea Surface Temperature\",                           \"\", 2, \"\"],         \r\n\"uas\":      [\"Eastward Near-Surface Wind\",                        \"\", 2, \"\"],            \r\n\"vas\":      [\"Northward Near-Surface Wind\",                       \"\", 2, \"\"],             \r\n\"sfcWind\":  [\"Near-Surface Wind Speed\",                           \"\", 2, \"\"],         \r\n\"zos\":      [\"Sea Surface Height\",                                \"\", 2, \"\"],    \r\n\"lai\":      [\"Leaf Area Index\",                                   \"\", 2, \"\"], \r\n\"zl\":       [\"Equivalent Water Height Over Land\",                 \"\", 2, \"\"],                   \r\n\"zo\":       [\"Equivalent Water Height Over Ocean\",                \"\", 2, \"\"],                    \r\n\"ohc700\":   [\"Ocean Heat Content Anomaly within 700 m Depth\",     \"\", 2, \"\"],                \r\n\"ohc2000\":  [\"Ocean Heat Content Anomaly within 2000 m Depth\",    \"\", 2, \"\"],                \r\n\"rlds\":     [\"Surface Downwelling Longwave Radiation\",            \"\", 2, \"\"],                        \r\n\"rsds\":     [\"Surface Downwelling Shortwave Radiation\",           \"\", 2, \"\"],                         \r\n\"rlus\":     [\"Surface Upwelling Longwave Radiation\",              \"\", 2, \"\"],                      \r\n\"rsus\":     [\"Surface Upwelling Shortwave Radiation\",             \"\", 2, \"\"],                       \r\n\"rldscs\":   [\"Surface Downwelling Clear-Sky Longwave Radiation\",  \"\", 2, \"\"],             \r\n\"rsdscs\":   [\"Surface Downwelling Clear-Sky Shortwave Radiation\", \"\", 2, \"\"],                   \r\n\"rsuscs\":   [\"Surface Upwelling Clear-Sky Shortwave Radiation\",   \"\", 2, \"\"],                \r\n\"rsdt\":     [\"TOA Incident Shortwave Radiation\",                  \"\", 2, \"\"],                  \r\n\"rlut\":     [\"TOA Outgoing Longwave Radiation\",                   \"\", 2, \"\"],                 \r\n\"rsut\":     [\"TOA Outgoing Shortwave Radiation\",                  \"\", 2, \"\"],                  \r\n\"rlutcs\":   [\"TOA Outgoing Clear-Sky Longwave Radiation\",         \"\", 2, \"\"],       \r\n\"rsutcs\":   [\"TOA Outgoing Clear-Sky Shortwave Radiation\",        \"\", 2, \"\"],              \r\n\"ta\":       [\"Air Temperature\",                                   \"\", 3, \"\"], \r\n\"hus\":      [\"Specific Humidity\",                                 \"\", 3, \"\"],   \r\n\"cli\":      [\"Cloud Ice Water Content\",                           \"\", 3, \"\"],         \r\n\"clw\":      [\"Cloud Liquid Water Content\",                        \"\", 3, \"\"],            \r\n\"ot\":       [\"Ocean Temperature\",                            \"ocean\", 3, \"\"],   \r\n\"os\":       [\"Ocean Salinity\",                               \"ocean\", 3, \"\"],\r\n\"wap\":      [\"Vertical Wind Velocity\",                            \"\", 3, \"\"],        \r\n\"hur\":      [\"Relative Humidity\",                                 \"\", 3, \"\"] \r\n}<script>function put_var_disable(thisID, thatID, rule) {\r\n\tvar thisStr =  document.getElementById(thisID).value;\r\n\tvar thatInputTobe = rule[thisStr][1][0];  \r\n\r\n\tif (thatInputTobe == \"true\") {\r\n\t\tdocument.getElementById(thatID).disabled = false;\r\n\t}else {\r\n\t\tdocument.getElementById(thatID).disabled = true;\r\n\t}\r\n}function put_var_input(thisID, thatID, rule) {\r\n\r\n\tvar thisStr =  document.getElementById(thisID).value;\r\n\tvar thatInputTobe = rule[thisStr][1][0];  \r\n\tconsole.log(thatInputTobe);\r\n\tdocument.getElementById(thatID).placeholder = thatInputTobe;\r\n\r\n}function put_var(thisID, thatID, rule) {\r\n\tvar thatList=document.getElementById(thatID);\r\n\t\r\n\tfor (var i=thatList.length-1; i>=0; i--) {\r\n\t  \tthatList.remove(i);\r\n\t} \r\n\r\n\tvar thisStr =  document.getElementById(thisID).value;\r\n\tvar thatListTobe = rule[thisStr][1];  \r\n\tfor (var i=0; i<thatListTobe.length; i++) {\r\n\t  \tvar k = thatListTobe[i];\r\n\t  \tthatList.add(new Option(varList[k][0],k));\r\n\t  \t\r\n\t}\r\n}";

	// html tail
	public static final String htmlTail1 = "</tbody>\r\n        </table>\r\n    </form>\r\n    <div class=\"text-center\">\r\n    \t<button type=\"submit\" class=\"btn btn-success btn-lg\" onclick=\"Javascript:sendValues('";
	public static final String htmlTail2 = "')\">Request Service</button>\r\n    </div>\r\n</div>\r\n\r\n<div class=\"container col-md-6\">\r\n    <form>\r\n        <table class=\"table table-bordered table-striped\">\r\n            <thead>\r\n            <tr>\r\n                <th>Output</th>\r\n            </tr>\r\n            </thead>\r\n            <tbody>\r\n            <tr>\r\n                <td>\r\n                    <a id=\"serviceImgLink\" href=\"\">\r\n                        <img src=\"\" id=\"serviceImg\" class=\"img-responsive\">\r\n                    </a>\r\n                </td>\r\n            </tr>\r\n            <tr>\r\n                <td>\r\n                    <a id=\"commentLink\" href=\"\">\r\n                        <textarea class=\"form-control\" rows=\"3\" id=\"comment\"></textarea>\r\n                    </a>\r\n                </td>\r\n            </tr>\r\n            <tr>\r\n                <td>\r\n                    <textarea class=\"form-control\" rows=\"10\" id=\"message\"></textarea>\r\n                </td>\r\n            </tr>\r\n            </tbody>\r\n        </table>\r\n        <div class=\"text-center\">\r\n            <button id = \"downloadButton\" type=\"button\" class=\"btn btn-success btn-lg\">Download Data</button>\r\n </div> <br> <div class=\"text-center\" id=\"output\">";
	public static final String htmlTail3 = "</div></form>\r\n</div>\r\n\r\n\r\n</body>\r\n</html>";
	//New service execution log stuff
	public static final String SERVICE_EXECUTION_LOG =	"/serviceExecutionLog";
	public static final String SERVICE_EXECUTION_LOG_QUERY =	"/queryServiceExecutionLogs";
	public static final String SERVICE_EXECUTION_LOG_GET= "/getServiceExecutionLog";
	public static final String NEW_GET_ALL_SERVICE_LOG = "/getAllServiceExecutionLog";

	//ServiceConfigItem
	public static final String CONFIG_ITEM =	"/serviceConfigurationItem";
	public static final String GET_CONFIG_ITEMS_BY_CONFIG= "/serviceConfigurationItemByServiceConfig";

	//Analytics
	public static final String GET_RELATIONAL_GRAPH = "/analytics/getRelationalKnowledgeGraph/json";
	public static final String GET_SHORTEST_PATH = "/graphAlgorithm/getShortestPath/source/";
	public static final String GET_KTH_SHORTEST_PATH = "/graphAlgorithm/getKthShortestPath/source/";

	//recommendation
	public static final String GET_TOP_K_USER_BASED_DATASET1 = "/getTopKUserBasedCFRecommendedDatasetByUsername?username=";
	public static final String GET_TOP_K_USER_BASED_DATASET2 = "&top_num=";
	public static final String GET_TOP_K_USER_BASED_DATASET_HYBRID1 = "/Hybrid_LDA_Based_AND_CF_Recommendation?username=";
	public static final String GET_TOP_K_USER_BASED_DATASET_HYBRID2 = "&top_num=";
  public static final String GET_TOP_K_USER_BASED_DATASET_HYBRID3 = "&text=";
	public static final String GET_TOP_K_ITEM_BASED_DATASET1 = "/getTopKItemBasedCFRecommendedDatasetByUsername?username=";
	public static final String GET_TOP_K_ITEM_BASED_DATASET2 = "&top_num=";
	public static final String GET_TOP_K_FEATURE_BASED_DATASET1 = "/getTopKLDABasedRecommendedDatasetByUsername?username=";
	public static final String GET_TOP_K_FEATURE_BASED_DATASET2 = "&top_num=";

	//workflow
	public static final String SEARCH_EXECUTION_LOGS_BY_USER = "/serviceExecutionLog/queryExecutionLogsByUser";
}
