<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Entry</title>
        <style>
            h1{
                color: red;
            }
            li{
                line-height:30px;
            }
        </style>
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
        </ol>
    </body>
</html>
