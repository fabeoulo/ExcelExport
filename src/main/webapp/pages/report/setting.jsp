<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authentication var="user" property="principal" />
<sec:authorize access="isAuthenticated()"  var="isLogin" />
<sec:authorize access="hasRole('USER')"  var="isUser" />
<sec:authorize access="hasRole('OPER')"  var="isOper" />
<sec:authorize access="hasRole('ADMIN')"  var="isAdmin" />

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Report Setting</title>
        <style>
            h1{
                color: red;
            }
            .box {
                width:1270px;
                padding:20px;
                background-color:#fff;
                border:1px solid #ccc;
                border-radius:5px;
                margin-top:25px;
            }
            th {
                font-size: 12px;
            }
            td {
                font-size: 11px;
            }
            .job-close {
                color: grey;
                opacity: 0.8;
            }
            .job-new {
                color: red;
            }
        </style>
        <link rel="stylesheet" href="<c:url value="/libs/bootstrap/bootstrap.css" />" />
        <link rel="stylesheet" href="<c:url value="/libs/datatables.net-dt/jquery.dataTables.css" />" />
        <link rel="stylesheet" href="<c:url value="/libs/datatables.net-fixedheader-dt/fixedHeader.dataTables.css" />"/>
        <link rel="stylesheet" href="<c:url value="/libs/datatables.net-select-dt/select.dataTables.css" />"/>
        <link rel="stylesheet" href="<c:url value="/libs/datatables.net-buttons-dt/buttons.dataTables.css" />"/>
        <link rel="stylesheet" href="<c:url value="/libs/bootstrap-datepicker/bootstrap-datepicker3.css" />"/>
        <link rel="stylesheet" href="<c:url value="/libs/font-awesome/font-awesome.min.css" />" />

        <script src="<c:url value="/libs/jQuery/jquery.js" />"></script>
        <script src="<c:url value="/libs/bootstrap/bootstrap.js" />"></script>
        <script src="<c:url value="/libs/datatables.net/jquery.dataTables.js" />"></script>
        <script src="<c:url value="/libs/datatables.net-fixedheader/dataTables.fixedHeader.js" />"></script>
        <script src="<c:url value="/libs/datatables.net-select/dataTables.select.js" />"></script>
        <script src="<c:url value="/libs/datatables.net-buttons/dataTables.buttons.js" />"></script>
        <script src="<c:url value="/libs/datatables.net-buttons/buttons.flash.js" />"></script>
        <script src="<c:url value="/libs/datatables.net-buttons/buttons.colVis.js" />"></script>
        <script src="<c:url value="/libs/datatables.net-buttons/buttons.html5.js" />"></script>
        <script src="<c:url value="/libs/datatables.net-buttons/buttons.print.js" />"></script>
        <script src="<c:url value="/libs/jszip/jszip.js" />"></script>
        <script src="<c:url value="/libs/bootstrap-datepicker/bootstrap-datepicker.js" />"></script>
        <script src="<c:url value="/extraJs/jquery.spring-friendly.js" />"></script>
        <script src="<c:url value="/libs/moment/moment.js" />"></script>
        <script src="<c:url value="/libs/jsog/JSOG.js" />"></script>
        <script src="<c:url value="/libs/remarkable-bootstrap-notify/bootstrap-notify.js" />"></script>
        <script>
            $(function () {
                var isEditor = true;

                var dataTable_config = {
                    "sPaginationType": "full_numbers",
                    "processing": true,
                    "serverSide": true,
                    "fixedHeader": true,
                    "orderCellsTop": true,
                    "ajax": {
                        "url": "<c:url value="/AchievingController/findAll" />",
                        "type": "GET",
                        "data": function (d) {
                        },
                        "dataSrc": function (json) {
                            return JSOG.decode(json.data);
                        }
                    },
                    "columns": [
                        {data: "id", title: "id"},
                        {data: "worktimeEstimated", title: "預估工時"},
                        {data: "outputValueEstimated", title: "預估產值"},
                        {data: "factory", title: "廠別"},
                        {data: "sapProductionType", title: "部別"}
                    ],
                    "columnDefs": [

                    ],
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_"
                    },
                    "initComplete": function (settings, json) {

                    },
                    "bAutoWidth": false,
                    "displayLength": 10,
                    "lengthChange": false,
                    "filter": false,
                    "info": true,
                    "paginate": false,
                    "select": true,
                    "searchDelay": 1000,
                    "order": [[0, "asc"]]
                };

                var successHandler = function (response) {
                    console.log("updated");
                    refreshTable();
                    ws.send("UPDATE");
                    $.notify('資料已更新', {placement: {
                            from: "bottom",
                            align: "right"
                        }
                    });
                };
                var errorHandler = function (xhr, ajaxOptions, thrownError) {
                    $("#dialog-msg").val(xhr.responseText);
                };

                var extraSetting2 = {
                    "dom": 'Bfrtip',
                    "buttons": [
                        'pageLength',
                        {
                            "text": '新增',
                            "attr": {
                                "data-toggle": "modal",
                                "data-target": "#myModal"
                            },
                            "action": function (e, dt, node, config) {
                                $("#model-table input").val("");
                                $("#model-table #id").val(0);
                            }
                        },
                        {
                            "text": '編輯',
                            "attr": {
                            },
                            "action": function (e, dt, node, config) {
//                                    if (isEditor) {
//                                        $("#model-table #po, #materialNumber, #amount").attr("disabled", true);
//                                    }

                                var cnt = table.rows('.selected').count();
                                if (cnt != 1) {
                                    alert("Please select a row.");
                                    return false;
                                }
                                $('#myModal').modal('show');
                                var arr = table.rows('.selected').data();
                                var data = arr[0];
                                $("#model-table #id").val(data.id);
                                $("#model-table #worktimeEstimated").val(data.worktimeEstimated);
                                $("#model-table #outputValueEstimated").val(data.outputValueEstimated);
                                $("#model-table #factory").val(data.factory);
                                $("#model-table #sapProductionType").val(data.sapProductionType);
                            }
                        },
                        {
                            "text": '刪除',
                            "attr": {
                            },
                            "action": function (e, dt, node, config) {
                                var cnt = table.rows('.selected').count();
                                if (cnt != 1) {
                                    alert("Please select a row.");
                                    return false;
                                }
                                if (confirm("Confirm delete?")) {
                                    var arr = table.rows('.selected').data();
                                    var data = arr[0];
                                    deleteRow(data);
                                }
                            }
                        }
                    ]
                };
                $.extend(dataTable_config, extraSetting2);

                var table = $('#favourable').DataTable(dataTable_config);

                $("#myModal #save").click(function () {
                    if (confirm("Confirm save?")) {
                        var data = {
                            id: $("#model-table #id").val(),
                            worktimeEstimated: $("#model-table #worktimeEstimated").val(),
                            outputValueEstimated: $("#model-table #outputValueEstimated").val(),
                            factory: $("#model-table #factory").val(),
                            sapProductionType: $("#model-table #sapProductionType").val()
                        };
                        save(data);
                    }
                });

                $("body").on("keyup", ":text", function () {
                    $(this).val($(this).val().trim().toLocaleUpperCase());
                });

                function save(data) {
                    $.ajax({
                        type: "POST",
                        url: "<c:url value="/AchievingController/save" />",
                        data: data,
                        success: function (response) {
                            alert(response);
                            $('#myModal').modal('toggle');
                            refreshTable();
                            $.notify('資料已更新', {placement: {
                                    from: "bottom",
                                    align: "right"
                                }
                            });
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            $("#dialog-msg").val(xhr.responseText);
                        }
                    });
                }

                function deleteRow(data) {
                    $.ajax({
                        type: "POST",
                        url: "<c:url value="/AchievingController/delete" />",
                        data: data,
                        success: function (response) {
                            alert(response);
                            refreshTable();
                            $.notify('資料已更新', {placement: {
                                    from: "bottom",
                                    align: "right"
                                }
                            });
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            $("#dialog-msg").val(xhr.responseText);
                        }
                    });
                }

                function refreshTable() {
                    table.ajax.reload();
                }

            });

        </script>
    </head>
    <body>
        <!-- Modal -->
        <div id="myModal" class="modal fade" role="dialog">
            <div class="modal-dialog">

                <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h4 id="titleMessage" class="modal-title"></h4>
                    </div>
                    <div class="modal-body">
                        <div>
                            <table id="model-table" cellspacing="10" class="table table-bordered">
                                <tr class="hide_col">
                                    <td class="lab">id</td>
                                    <td>
                                        <input type="text" id="id" value="0" disabled="true" readonly>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">工時預估</td>
                                    <td> 
                                        <input type="number" id="worktimeEstimated">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">產值預估</td>
                                    <td> 
                                        <input type="number" id="outputValueEstimated">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">factory</td>
                                    <td>
                                        <input type="text" id="factory" />
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">部別</td>
                                    <td>
                                        <input type="text" id="sapProductionType" />
                                    </td>
                                </tr>
                            </table>
                            <div id="dialog-msg" class="alarm"></div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" id="save" class="btn btn-default">Save</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                </div>

            </div>
        </div>

        <div class="container box">
            <small>
                <div class="table-responsive">
                    <!--<h1 align="center">Requisition details search</h1>-->

                    <c:if test="${isLogin}">
                        <h5>
                            Hello, <c:out value="${user.username}" /> /
                            <a href="<c:url value="/logout" />">登出</a>
                        </h5>
                    </c:if>

                    <table class="table table-bordered table-hover" id="favourable">
                    </table>
                </div>
            </small>
        </div>
        <div>
            <a href="<c:url value="/template.jsp" />"></a>
        </div>
    </body>
</html>
