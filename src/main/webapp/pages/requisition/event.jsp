<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authentication var="user" property="principal" />
<sec:authorize access="isAuthenticated()"  var="isLogin" />
<c:if test="${!isLogin || param.requisition_id == null}">
    <c:redirect url="/" />
</c:if>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${initParam.pageTitle} - Requisition</title>
        <style>
            h1{
                color: red;
            }
            thead input {
                width: 100%;
            }
            .box {
                width:1270px;
                padding:20px;
                background-color:#fff;
                border:1px solid #ccc;
                border-radius:5px;
                margin-top:25px;
            }
        </style>
        <link rel="stylesheet" href="<c:url value="/libs/bootstrap/bootstrap.css" />" />
        <link rel="stylesheet" href="<c:url value="/libs/datatables.net-dt/jquery.dataTables.css" />" />
        <link rel="stylesheet" href="<c:url value="/libs/datatables.net-fixedheader-dt/fixedHeader.dataTables.css" />"/>
        <link rel="stylesheet" href="<c:url value="/libs/datatables.net-select-dt/select.dataTables.css" />"/>
        <link rel="stylesheet" href="<c:url value="/libs/datatables.net-buttons-dt/buttons.dataTables.css" />"/>
        <link rel="stylesheet" href="<c:url value="/libs/bootstrap-datepicker/bootstrap-datepicker3.css" />"/>

        <script src="<c:url value="/libs/jQuery/jquery.js" />"></script>
        <script src="<c:url value="/libs/bootstrap/bootstrap.js" />"></script>
        <script src="<c:url value="/libs/datatables.net/jquery.dataTables.js" />"></script>
        <script src="<c:url value="/libs/datatables.net-fixedheader/dataTables.fixedHeader.js" />"></script>
        <script src="<c:url value="/libs/datatables.net-select/dataTables.select.js" />"></script>
        <script src="<c:url value="/libs/datatables.net-buttons/dataTables.buttons.js" />"></script>
        <script src="<c:url value="/libs/jszip/jszip.js" />"></script>
        <script src="<c:url value="/libs/bootstrap-datepicker/bootstrap-datepicker.js" />"></script>
        <script src="<c:url value="/extraJs/jquery.spring-friendly.js" />"></script>
        <script src="<c:url value="/libs/moment/moment.js" />"></script>
        <script src="<c:url value="/libs/jsog/JSOG.js" />"></script>
        <script>
            $(function () {
                var requisition_id = '${param.requisition_id}';

                var dataTable_config = {
                    "processing": true,
                    "serverSide": true,
                    "fixedHeader": true,
                    "orderCellsTop": true,
                    "ajax": {
                        "url": "<c:url value="/RequisitionController/findEvent" />",
                        "type": "POST",
                        "data": {
                            requisition_id: requisition_id
                        },
                        "dataSrc": function (json) {
                            return JSOG.decode(json.data);
                        }
                    },
                    "columns": [
                        {data: "id"},
                        {data: "user.username"},
                        {data: "amount"},
                        {data: "requisitionState.name"},
                        {data: "requisitionReason.name"},
                        {data: "requisitionCateIms.name"},
                        {data: "requisitionCateMes.name"},
                        {data: "requisitionType.name"},
                        {data: "modifiedDate"},
                        {data: "remark"},
                        {data: "agent"},
                        {data: "floor.name"}
                    ],
                    "columnDefs": [
                        {
                            "targets": [0],
                            "visible": true
                        },
                        {
                            "targets": [8],
                            'render': function (data, type, full, meta) {
                                return data == null ? "n/a" : formatDate(data);
                            }
                        },
                        {
                            "targets": [6],
                            'render': function (data, type, full, meta) {
                                return data ? data :
                                        full.requisitionCateMesCustom ? full.requisitionCateMesCustom : "n/a";
                            }
                        },
                        {
                            "targets": [4, 5, 6, 7, 9, 10, 11],
                            'render': function (data, type, full, meta) {
                                return data == null ? "n/a" : data;
                            }
                        }
                    ],
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_"
                    },
                    "bAutoWidth": false,
                    "displayLength": 50,
                    "lengthChange": false,
                    "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
                    "filter": false,
                    "info": false,
                    "paginate": false,
                    "order": [[0, "asc"]]
                };

                var table = $('#favourable').DataTable(dataTable_config);

                $(document).ajaxStart(function () {
                    $("input").not(".search_disabled").attr("disabled", true);
                }).ajaxStop(function () {
                    $("input").not(".search_disabled").removeAttr("disabled");
                });

                function formatDate(ds) {
                    console.log(moment(ds));
                    return moment.utc(ds).format('YYYY/MM/DD HH:mm:ss'); // October 22nd 2018, 10:37:08 am
                }

            });

        </script>
    </head>
    <body>
        <div class="container box">
            <div class="table-responsive">
                <table class="table table-bordered table-hover" id="favourable">
                    <thead>
                        <tr>
                            <th>id</th>
                            <th>人員</th>
                            <th>數量</th>
                            <th>申請狀態</th>
                            <th>產線判定</th>
                            <th>材料類別</th>	
                            <th>異常分類</th>
                            <th>料號狀態</th>
                            <th>異動日期</th>
                            <th>備註</th>
                            <th>代理</th>
                            <th>位置</th>
                        </tr>
                    </thead>
                </table>
            </div>
        </div>
    </body>
</html>
