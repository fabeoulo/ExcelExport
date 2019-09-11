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
        <title>${initParam.pageTitle} - Requisition</title>
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
            th { font-size: 12px; }
            td { font-size: 11px; }
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
                initDropDownOptions();
                var isEditor = ${isOper || isAdmin};
                console.log(isEditor);

                var dataTable_config = {
                    "sPaginationType": "full_numbers",
                    "processing": true,
                    "serverSide": true,
                    "fixedHeader": true,
                    "orderCellsTop": true,
                    "ajax": {
                        "url": "<c:url value="/RequisitionController/findAll" />",
                        "type": "POST",
                        "data": function (d) {
//                            https://medium.com/code-kings/datatables-js-how-to-update-your-data-object-for-ajax-json-data-retrieval-c1ac832d7aa5
                            d.startDate = $("#datepicker_from").val();
                            d.endDate = $("#datepicker_to").val();
                        },
                        "dataSrc": function (json) {
                            return JSOG.decode(json.data);
                        }
                    },
                    "columns": [
                        {data: "id", title: "id"},
                        {data: "po", title: "工單"},
                        {data: "materialNumber", title: "料號"},
                        {data: "amount", title: "數量"},
                        {data: "requisitionReason.name", "defaultContent": "n/a", title: "原因"},
                        {data: "user.username", "defaultContent": "n/a", title: "申請人"},
                        {data: "requisitionState.name", "defaultContent": "n/a", title: "申請狀態"},
                        {data: "createDate", title: "申請日期"},
                        {data: "receiveDate", title: "領料日期"},
                        {data: "returnDate", title: "退料日期"},
                        {data: "requisitionType.name", "defaultContent": "n/a", title: "料號狀態"},
                        {data: "materialType", "defaultContent": "n/a", title: "分類"},
                        {data: "remark", "defaultContent": "n/a", title: "備註"},
                        {data: "id", "defaultContent": "n/a", title: "紀錄"}
                    ],
                    "columnDefs": [
                        {
                            "targets": [0, 7],
                            "visible": false,
                            "searchable": false
                        },
                        {
                            "targets": [11, 12],
                            "visible": isEditor,
                            "searchable": false
                        },
                        {
                            "targets": [7, 8, 9],
                            "searchable": false,
                            'render': function (data, type, full, meta) {
                                return data == null ? "n/a" : formatDate(data);
                            }
                        },
                        {
                            "targets": [13],
                            "searchable": false,
                            'render': function (data, type, full, meta) {
                                return "<a href='event.jsp?requisition_id=" + data + "' target='_blank'>紀錄</a>";
                            }
                        }
                    ],
                    "createdRow": function (row, data, dataIndex) {
                        var state = data.requisitionState.id;
                        $(row).addClass('text text-' + (state == 7 || state == 2 ? 'muted' : (state == 4 ? 'danger' : 'primary')));
                    },
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_"
                    },
                    "initComplete": function (settings, json) {
                        connectToServer();
                    },
                    "bAutoWidth": false,
                    "displayLength": 10,
                    "lengthChange": true,
                    "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
                    "filter": true,
                    "info": true,
                    "paginate": true,
                    "select": true,
                    "searchDelay": 1000,
                    "order": [[0, "desc"]]
                };

                if (${isUser && !isOper}) {
                    var extraSetting = {
                        "dom": 'Bfrtip',
                        "buttons": [
                            'pageLength',
//                            {
//                                "text": '需求申請',
//                                "attr": {
//                                    "data-toggle": "modal",
//                                    "data-target": "#myModal"
//                                },
//                                "action": function (e, dt, node, config) {
//                                    $("#model-table input").val("");
//                                    $("#model-table #id").val(0);
//                                }
//                            },
                            {
                                "text": '需求申請',
                                "attr": {
                                    "data-toggle": "modal",
                                    "data-target": "#myModal2"
                                },
                                "action": function (e, dt, node, config) {
                                    $("#model-table input").val("");
                                    $("#model-table #id").val(0);
                                }
                            }
                        ]
                    };
                    $.extend(dataTable_config, extraSetting);
                } else if (${isOper}) {
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
//                            {
//                                "text": '新增需求',
//                                "attr": {
//                                    "data-toggle": "modal",
//                                    "data-target": "#myModal"
//                                },
//                                "action": function (e, dt, node, config) {
//                                    $("#model-table input").val("");
//                                    $("#model-table #id").val(0);
//                                }
//                            },
                            {
                                "text": '新增需求',
                                "attr": {
                                    "data-toggle": "modal",
                                    "data-target": "#myModal2"
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
                                    $("#model-table #po").val(data.po);
                                    $("#model-table #materialNumber").val(data.materialNumber);
                                    $("#model-table #amount").val(data.amount);
                                    $("#model-table #requisitionReason\\.id").val(data.requisitionReason.id);
                                    $("#model-table #requisitionState\\.id").val(data.requisitionState.id);
                                    $("#model-table #requisitionType\\.id").val('requisitionType' in data && data.requisitionType != null ? data.requisitionType.id : 1);
                                    $("#model-table #user\\.id").val(data.user.id);
                                    $("#model-table #materialType").val(data.materialType);
                                    $("#model-table #remark").val(data.remark);
                                    $("#model-table #receiveDate").val(data.receiveDate);
                                    $("#model-table #returnDate").val(data.returnDate);
                                }
                            },
                            {
                                "extend": 'excel',
                                "exportOptions": {
                                    "columns": 'th:not(:first-child):not(:last-child)',
                                    modifier: {
                                        // DataTables core
                                        search: 'applied',
                                        order: 'applied'
                                    }
                                }
                            }
                        ]
                    };
                    $.extend(dataTable_config, extraSetting2);
                }


                $('#favourable thead tr').clone(true).appendTo('#favourable thead');
                $('#favourable thead tr:eq(1) th').each(function (i) {
                    var title = $(this).text();
                    $(this).html('<input type="text" id=_header_fitler_' + title + ' placeholder="Search ' + title + '" />');
                    $('input', this).on('keyup change', function () {
                        if (table.column(i).search() !== this.value) {
                            table
                                    .column(i)
                                    .search(this.value)
                                    .draw();
                        }
                    });
                });

//                $("#_header_fitler_日期").addClass("search_disabled").attr("disabled", true);

                var table = $('#favourable').DataTable(dataTable_config);

                $("#datepicker_from, #datepicker_to").datepicker({
                    format: "yyyy-mm-dd",
                    showOn: "button",
                    buttonImage: "images/calendar.gif",
                    buttonImageOnly: false
                });

                $("#search").click(function () {
                    table.ajax.reload();
                });

                $("#clear").click(function () {
                    $(":text, input[type='search']").val("");
                    table.search('').columns().search('').draw();
                });

                $(document).ajaxStart(function () {
                    $("input").not(".search_disabled, div.dataTables_filter input").attr("disabled", true);
                }).ajaxStop(function () {
                    $("input").not(".search_disabled").removeAttr("disabled");
                });

                $("#myModal #save").click(function () {
                    if (confirm("Confirm save?")) {
                        var amount = $("#model-table #amount").val();
                        var po = $("#model-table #po").val(), m = $("#model-table #materialNumber").val();
                        if (isNaN(amount) || amount == "") {
                            alert("Amount please insert a number.");
                            return false;
                        }
                        if (po == "" || m == "") {
                            alert("Po or MaterialNumber can't be empty.");
                            return false;
                        }
                        var data = {
                            id: $("#model-table #id").val(),
                            po: po,
                            materialNumber: m,
                            amount: amount,
                            "requisitionReason.id": $("#model-table #requisitionReason\\.id").val(),
                            "requisitionState.id": $("#model-table #requisitionState\\.id").val(),
                            "requisitionType.id": $("#model-table #requisitionType\\.id").val(),
                            "user.id": $("#model-table #user\\.id").val(),
                            "materialType": $("#model-table #materialType").val(),
                            remark: $("#model-table #remark").val(),
                            receiveDate: $("#model-table #receiveDate").val(),
                            returnDate: $("#model-table #returnDate").val()
                        };
                        if (data.id == 0) {
                            delete data["user.id"];
                        }
                        save(data);
                    }
                });

                $("#myModal2 #save").click(function () {
                    if (confirm("Confirm save?")) {
                        var tb = $("#material-detail tbody tr");
                        var po = $("#model-table2 #po").val();
                        var myArray = tb.map(function () {
                            var o = {
                                po: po,
                                materialNumber: $(this).find("input").eq(0).val(),
                                amount: $(this).find("input").eq(1).val(),
                                remark: $(this).find("#remark").val()
                            };
                            return o;
                        }).get();
                        var data = {
                            "myList": myArray
                        };
                        batchSave(data);
                    }
                });

                $("body").on("keyup", "#model-table #po, #model-table #materialNumber", function () {
                    $(this).val($(this).val().trim().toLocaleUpperCase());
                });

                $("#myModal2 #add-material").click(function () {
                    var first = $("#myModal2 #material-detail").find("tbody>tr").eq(0);
                    var clone = first.clone(true);
                    clone.find("input").val("");
                    clone.find("textarea").html("");
                    first.after(clone);
                });

                $("#myModal2 .remove-material").click(function () {
                    var length = $("#myModal2 #material-detail").find("tbody>tr").length;
                    if (length > 1) {
                        $(this).closest("tr").remove();
                    }
                });

                $(".hide_col").hide();

                enterToTab();

                $("#myModal2").find("input, select, textarea").addClass("form-control");

                function formatDate(ds) {
//                    console.log(moment(ds));
                    return moment.utc(ds).format('YY/MM/DD HH:mm'); // October 22nd 2018, 10:37:08 am
                }

                function save(data) {
                    $.ajax({
                        type: "POST",
                        url: "<c:url value="/RequisitionController/save" />",
                        data: data,
                        success: function (response) {
                            alert(response);
                            $('#myModal').modal('toggle');
                            refreshTable();
                            ws.send("ADD");
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

                function batchSave(data) {
                    $.ajax({
                        type: "POST",
                        url: "<c:url value="/RequisitionController/batchSave" />",
                        dataType: "json",
                        data: data,
                        success: function (response) {
                            alert(response);
                            $('#myModal2').modal('toggle');
                            refreshTable();
                            ws.send("ADD");
                            $.notify('資料已更新', {placement: {
                                    from: "bottom",
                                    align: "right"
                                }
                            });
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            $("#dialog-msg2").val(xhr.responseText);
                        }
                    });
                }

                function initDropDownOptions() {
                    $.ajax({
                        type: "GET",
                        url: "<c:url value="/RequisitionController/findRequisitionReasonOptions" />",
                        success: function (response) {
                            var sel = $("#model-table #requisitionReason\\.id");
                            var d = response;
                            for (var i = 0; i < d.length; i++) {
                                var options = d[i];
                                sel.append("<option value='" + options.id + "'>" + options.name + "</option>");
                            }
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    });
                    $.ajax({
                        type: "GET",
                        url: "<c:url value="/RequisitionController/findRequisitionStateOptions" />",
                        success: function (response) {
                            var sel = $("#model-table #requisitionState\\.id");
                            var d = response;
                            for (var i = 0; i < d.length; i++) {
                                var options = d[i];
                                sel.append("<option value='" + options.id + "'>" + options.name + "</option>");
                            }
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    });
                    $.ajax({
                        type: "GET",
                        url: "<c:url value="/RequisitionController/findRequisitionTypeOptions" />",
                        success: function (response) {
                            var sel = $("#model-table #requisitionType\\.id");
                            var d = response;
                            for (var i = 0; i < d.length; i++) {
                                var options = d[i];
                                sel.append("<option value='" + options.id + "'>" + options.name + "</option>");
                            }
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    });
                }

                function refreshTable() {
                    table.ajax.reload();
                }

                // 按下Enter轉成按下Tab
                function enterToTab()
                {
                    $('input').on("keypress", function (e) {
                        /* ENTER PRESSED*/
                        if (e.keyCode == 13) {
                            /* FOCUS ELEMENT */
                            var inputs = $(this).parents("table").eq(0).find(":input");
                            var idx = inputs.index(this);

                            if (idx == inputs.length - 1) {
                                inputs[0].select();
                            } else {
                                inputs[idx + 1].focus(); //  handles submit buttons
                                inputs[idx + 1].select();
                            }
                            return false;
                        }
                    });
                }

                //Websocket connect part
                var hostname = window.location.host;//Get the host ip address to link to the server.

                var ws;
                var wsFailMsg = $("#ws-connect-fail-message");
                function connectToServer() {

                    try {
                        ws = new WebSocket("ws://" + hostname + "/ExcelReport/myHandler");

                        ws.onopen = function () {
                            wsFailMsg.remove();
                            console.log("Connected");
                        };
                        ws.onmessage = function (event) {
                            var d = event.data;
                            d = d.replace(/\"/g, "");
                            console.log(d);
                            if (("ADD" == d || "REMOVE" == d)) {
                                refreshTable();
                                if (isEditor) {
                                    $.notify('資料已更新', {placement: {
                                            from: "bottom",
                                            align: "right"
                                        }
                                    });
                                }
                            }
                        };
                        ws.onclose = function () {
                            console.log("Disconnected");
                        };
                    } catch (e) {
                        console.log(e);
                    }


                }
                function disconnectToServer() {
                    ws.close();
                }

                if (ws != null) {
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
                                    <td class="lab">工單</td>
                                    <td> 
                                        <input type="text" id="po">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">料號</td>
                                    <td>
                                        <input type="text" id="materialNumber" />
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">數量</td>
                                    <td>
                                        <input type="number" id="amount">
                                    </td>
                                </tr>

                                <c:if test="${isUser && (!isOper || !isAdmin)}">
                                    <tr class="hide_col">
                                        <td class="lab">原因</td>
                                        <td>
                                            <select id="requisitionReason.id"></select>
                                        </td>
                                    </tr>
                                </c:if>

                                <c:if test="${isOper || isAdmin}">
                                    <tr>
                                        <td class="lab">原因</td>
                                        <td>
                                            <select id="requisitionReason.id"></select>
                                        </td>
                                    </tr>
                                    <tr class="hide_col">
                                        <td class="lab">user_id</td>
                                        <td>
                                            <input type="text" id="user.id" disabled="true" readonly>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">申請狀態</td>
                                        <td>
                                            <select id="requisitionState.id"></select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">料號狀態</td>
                                        <td>
                                            <select id="requisitionType.id"></select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">分類</td>
                                        <td>
                                            <input type="text" id="materialType">
                                        </td>
                                    </tr>
                                    <tr class="hide_col">
                                        <td class="lab">領料日期</td>
                                        <td>
                                            <input type="text" id="receiveDate">
                                        </td>
                                    </tr>
                                    <tr class="hide_col">
                                        <td class="lab">退料日期</td>
                                        <td>
                                            <input type="text" id="returnDate">
                                        </td>
                                    </tr>
                                </c:if>
                                <tr>
                                    <td class="lab">備註</td>
                                    <td>
                                        <textarea id="remark"></textarea>
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

        <!-- Modal -->
        <div id="myModal2" class="modal fade" role="dialog">
            <div class="modal-dialog modal-lg">

                <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 id="titleMessage2" class="modal-title">Batch update</h4>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div>
                            <table id="model-table2" cellspacing="10" class="table table-bordered">
                                <tr class="hide_col">
                                    <td class="lab">id</td>
                                    <td>
                                        <input type="text" id="id" value="0" disabled="true" readonly>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">工單</td>
                                    <td> 
                                        <input type="text" id="po">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">詳細</td>
                                    <td>
                                        <table id="material-detail" class="table table-bordered">
                                            <thead>
                                                <tr>
                                                    <th>料號</th>
                                                    <th>數量</th>
                                                    <th>備註</th>
                                                    <th>動作</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <tr>
                                                    <td>
                                                        <input type="text" id="materialNumber" />
                                                    </td>
                                                    <td>
                                                        <input type="number" id="amount" />
                                                    </td>
                                                    <td>
                                                        <textarea id="remark" ></textarea>
                                                    </td>
                                                    <td>
                                                        <button type="button" class="btn btn-default btn-sm remove-material btn-outline-dark" aria-label="Left Align">
                                                            <span class="fa fa-remove" aria-hidden="true"></span>
                                                        </button>
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                        <div class="material-detail-footer">
                                            <button type="button" class="btn btn-default btn-sm btn-outline-dark" id="add-material">新增料號</button>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                            <div id="dialog-msg2" class="alarm"></div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" id="save" class="btn btn-default btn-outline-dark">Save</button>
                        <button type="button" class="btn btn-default btn-outline-dark" data-dismiss="modal">Close</button>
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

                    <h5 class="text-danger" id="ws-connect-fail-message">※因網頁不支援某些功能無法自動重整, 請手動按右方的Search button重新整理表格</h5>
                    <div class="row">
                        <div id="date_filter" class="input-daterange form-inline">
                            <div class="col-md-12">
                                <span id="date-label-from" class="date-label">From: </span><input class="date_range_filter date form-control" type="text" id="datepicker_from" placeholder="請選擇起始時間" />
                                <span id="date-label-to" class="date-label">To:<input class="date_range_filter date form-control" type="text" id="datepicker_to"  placeholder="請選擇結束時間"/>
                                    <input type="button" id="search" class="form-control" value="搜尋" />
                                    <input type="button" id="clear" class="form-control" value="清除搜尋" />
                            </div>
                        </div>
                    </div>
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
