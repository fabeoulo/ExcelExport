<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Entry</title>
        <link rel="stylesheet" href="<c:url value="/webjars/jquery-ui-themes/1.12.1/redmond/jquery-ui.min.css" />" >
        <style>
            h1{
                color: red;
            }
            li{
                line-height:30px;
            }
        </style>
        <script src="<c:url value="/webjars/jquery/1.12.4/jquery.min.js" />"></script>
        <script src="<c:url value="/extraJs/jquery-ui-1.10.0.custom.min.js" />"></script>
        <script src="<c:url value="/extraJs/jquery.fileDownload.js" />"></script>

        <script>
            $(function () {
                $("#generateExcel").click(function () {
                    $.fileDownload('<c:url value="/Excel/downloadReturn" />' + '?date='
                            , {
                                preparingMessageHtml: "We are preparing your report, please wait...",
                                failMessageHtml: "No reports generated. No Survey data is available.",
                                successCallback: function (url) {
                                },
                                failCallback: function (html, url) {
                                }
                            });
                });
            });
        </script>
    </head>
    <body>
        <h1>入口</h1>
        <ol>
            <li><a href="report.jsp">報廢統計</a></li>
            <li><a href="pages/requisition">快速領料平台</a></li>
            <li>
                <details>
                    <summary>代理開單</summary>
                    <ul>
                        <li><a href="pages/requisition/oper/userAgent.jsp">代理開單維護</a></li>
                        <li><a href="pages/requisition/oper/userAgent_label.jsp">標籤代理人</a></li>
                    </ul>
                </details>
            </li>
            <!--<li>
                <details>
                    <summary>IE 設定</summary>
                    <ul>
                        <li><a href="pages/report/setting.jsp">當月預估達成產值維護</a></li>
                        <li><a href="pages/report/workdays.jsp">休假補班維護</a></li>
                    </ul>
                </details>
            </li>-->
			<li><a href="#" id="generateExcel">不良品報表</a></li>
        </ol>
    </body>
</html>
