package utils;
public class Constants {

	public static final String URL_HOST = "http://localhost";
	public static final String URL_SERVER = "http://einstein.sv.cmu.edu";
	//public static final String URL_SERVER = "http://localhost";
	
	// port
	public static final String JPL_BACKEND_PORT = ":9002";
	public static final String LOCAL_HOST_PORT = ":9032";
	public static final String CMU_BACKEND_PORT = ":9035"; 

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
	
	// climate service page
	public static final String SAVE_CLIMATE_SERVICE_PAGE = "/climateService/savePage";
	
	// user
	public static final String IS_USER_VALID = "/users/isUserValid";
	public static final String ADD_USER = "/users/add";
	public static final String IS_EMAIL_EXISTED = "/users/isEmailExisted";
	
	//climate service execution log 
	public static final String GET_ALL_SERVICE_LOG = "/serviceExecutionLog/getAllServiceExecutionLog";	
	public static final String QUERY_SERVICE_LOG = "/serviceExecutionLog/queryServiceExecutionLogs";

	// dataset
	public static final String GET_ALL_DATASETS = "/dataset/getAllDatasets/json";
	
	// users
	public static final String GET_ALL_USERS = "/users/getAllUsers/json";
	
	// bug report
	public static final String ADD_BUG_REPORT = "/bugReport/addBugReport";
	public static final String GET_ALL_BUG_REPORTS = "/bugReport/getAllBugReports/json";
	public static final String DELETE_ONE_BUG_REPORT = "/bugReport/deleteBugReport/id/";
	public static final String UPDATE_BUG_REPORT = "/bugReport/updateBugReport/id/";
	public static final String GET_BUG_REPORT_BY_ID = "/bugReport/getBugReport/id/";
	
	// http://www.freeformatter.com/java-dotnet-escape.html
	// html head
	// public static final String htmlHead = "<head>\r\n    <meta charset=\"utf-8\">\r\n    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->\r\n    <title>Climate Service</title>\r\n    \r\n    <script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-2.1.4.min.js\"></script>\r\n    <script type=\"text/javascript\" src=\"/assets/javascripts/parameter.js\"></script>\r\n\r\n    <!-- Bootstrap -->\r\n    <link href=\"/assets/stylesheets/bootstrap.min.css\" rel=\"stylesheet\">\r\n\r\n    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->\r\n    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->\r\n    <!--[if lt IE 9]>\r\n    <script src=\"https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js\"></script>\r\n    <script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script>\r\n    <![endif]-->\r\n</head>\r\n<body>\r\n\r\n<h2 class=\"text-center\">Service: 2-D Variable Map</h2>\r\n\r\n<p class=\"text-center col-md-8 col-md-offset-2\">This service generates a map of a 2-dimensional variable with time\r\n    averaging and spatial\r\n    subsetting. Select a data source (model or observation), a variable name, a time range, and a spatial range\r\n    (lat-lon box) below.\r\n</p>\r\n\r\n<div class=\"container col-md-6\">\r\n    <form>\r\n        <table class=\"table table-bordered table-striped\">\r\n            <thead>\r\n            <tr>\r\n                <th class=\"col-md-2\">Parameter Name</th>\r\n                <th class=\"col-md-4\">Value</th>\r\n            </tr>\r\n            </thead>\r\n            <tbody id=\"dynamicTBody\">";
	
	public static final String htmlHead1 = "<head>\r\n    <meta charset=\"utf-8\">\r\n    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->\r\n    <title>Climate Service</title>\r\n    \r\n    <script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-2.1.4.min.js\"></script>\r\n    <script type=\"text/javascript\" src=\"/assets/javascripts/parameter.js\"></script>\r\n\r\n   ";
	public static final String htmlHead2	=	" </script><!-- Bootstrap -->\r\n    <link href=\"/assets/stylesheets/bootstrap.min.css\" rel=\"stylesheet\">\r\n\r\n    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->\r\n    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->\r\n    <!--[if lt IE 9]>\r\n    <script src=\"https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js\"></script>\r\n    <script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script>\r\n    <![endif]-->\r\n</head>\r\n<body>\r\n\r\n<h2 class=\"text-center\">";
	public static final String htmlHead3 = "</h2>\r\n\r\n<p class=\"text-center col-md-8 col-md-offset-2\">";
	public static final String htmlHead4 = "</p>\r\n\r\n<div class=\"container col-md-6\">\r\n    <form>\r\n        <table class=\"table table-bordered table-striped\">\r\n            <thead>\r\n            <tr>\r\n                <th class=\"col-md-2\">Parameter Name</th>\r\n                <th class=\"col-md-4\">Value</th>\r\n            </tr>\r\n            </thead>\r\n            <tbody id=\"dynamicTBody\">";
	public static final String putVar = "<script>\r\nfunction put_var(thisID, thatID, rule) {\r\n\tvar thatList=document.getElementById(thatID);\r\n\t\r\n\tfor (var i=thatList.length-1; i>=0; i--) {\r\n\t  \tthatList.remove(i);\r\n\t} \r\n\r\n\tvar thisStr =  document.getElementById(thisID).value;\r\n\tvar thatListTobe = rule[thisStr][1];  \r\n\tfor (var i=0; i<thatListTobe.length; i++) {\r\n\t  \tvar k = thatListTobe[i];\r\n\t  \tthatList.add(new Option(k,k));\r\n\t  \t\r\n\t}\r\n}";
	
	// html tail
	//public static final String htmlTail = "</tbody>\r\n        </table>\r\n    </form>\r\n    <div class=\"text-center\">\r\n    \t<button type=\"submit\" class=\"btn btn-success btn-lg\" onclick=\"Javascript:sendValues()\">Get Plot</button>\r\n    </div>\r\n</div>\r\n\r\n<div class=\"container col-md-6\">\r\n    <form>\r\n        <table class=\"table table-bordered table-striped\">\r\n            <thead>\r\n            <tr>\r\n                <th>Output</th>\r\n            </tr>\r\n            </thead>\r\n            <tbody>\r\n            <tr>\r\n                <td>\r\n                    <a id=\"serviceImgLink\" href=\"\">\r\n                        <img src=\"\" id=\"serviceImg\" class=\"img-responsive\">\r\n                    </a>\r\n                </td>\r\n            </tr>\r\n            <tr>\r\n                <td>\r\n                    <a id=\"commentLink\" href=\"\">\r\n                        <textarea class=\"form-control\" rows=\"3\" id=\"comment\"></textarea>\r\n                    </a>\r\n                </td>\r\n            </tr>\r\n            <tr>\r\n                <td>\r\n                    <textarea class=\"form-control\" rows=\"10\" id=\"message\"></textarea>\r\n                </td>\r\n            </tr>\r\n            </tbody>\r\n        </table>\r\n        <div class=\"text-center\">\r\n            <button type=\"submit\" class=\"btn btn-success btn-lg\">Download Data</button>\r\n        </div>\r\n    </form>\r\n</div>\r\n\r\n<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->\r\n<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js\"></script>\r\n<!-- Include all compiled plugins (below), or include individual files as needed -->\r\n<script src=\"/assets/javascripts/bootstrap.min.js\"></script>\r\n</body>\r\n</html>";
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
	
	
}
