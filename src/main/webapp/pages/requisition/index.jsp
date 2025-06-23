<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authentication var="user" property="principal" />
<sec:authorize access="isAuthenticated()"  var="isLogin" />
<sec:authorize access="hasRole('USER')"  var="isUser" />
<sec:authorize access="hasRole('OPER')"  var="isOper" />
<sec:authorize access="hasRole('ADMIN')"  var="isAdmin" />
<sec:authorize access="hasRole('OPER_WH')"  var="isOperWh" />
<sec:authorize access="hasRole('OPER_M8')"  var="isOperM8" />

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
            .alarm {
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
        <!--<script src="<c:url value="/libs/lodash/lodash.js" />"></script>-->
        <script>
            var table;
            var ws;

            $(function () {
                const eventColumnIndex = 22, cateMesColumnIndex = 10;

                const userUnitId = <c:out value="${user.unit.id}" />;
                const userFloorId = <c:out value="${user.floor.id}" />;
                const isEditor = ${isOper || isAdmin || isOperM8};
                const isInsertWh = ${isOperWh};
                const isM8Oper = ${isOperM8};
                const isM8User = userFloorId === 6;

                const lengthList = [[10, 25, 50, 100, 250], [10, 25, 50, 100, 250]];
                if (isEditor) {
                    lengthList[0].push(-1);
                    lengthList[1].push('all');
                }

                initDropDownOptionAndEvent();

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
                        {data: "po", title: "工單號碼", className: "excel_export"},
                        {data: "modelName", title: "機種", className: "excel_export"},
                        {data: "poQty", title: "工單數量", className: "excel_export"},
                        {data: "materialNumber", title: "料號", className: "excel_export"},
                        {data: "amount", title: "數量", className: "excel_export"},
                        {data: "unitPrice", title: "單價", className: "excel_export"},
                        {data: "requisitionFlow.name", "defaultContent": "n/a", title: "製程", className: "excel_export"},
                        {data: "requisitionReason.name", "defaultContent": "n/a", title: "產線判定", className: "excel_export"},
                        {data: "requisitionCateIms.name", "defaultContent": "n/a", title: "材料類別", className: "excel_export"},
                        {data: "requisitionCateMes.name", "defaultContent": "n/a", title: "異常分類", className: "excel_export"},
                        {data: "materialType", "defaultContent": "", title: "不良原因", className: "excel_export"},
                        {data: "materialBoardSn", "defaultContent": "", title: "材料序號", className: "excel_export"},
                        {data: "user.username", "defaultContent": "n/a", title: "申請人", className: "excel_export"},
                        {data: "floor.name", "defaultContent": "n/a", title: "位置", className: "excel_export"},
                        {data: "user.unit.name", "defaultContent": "n/a", title: "單位", visible: false},
                        {data: "requisitionState.name", "defaultContent": "n/a", title: "申請狀態", className: "excel_export"},
                        {data: "createDate", title: "申請日期", className: "excel_export"},
                        {data: "receiveDate", title: "領料日期", className: "excel_export"},
                        {data: "returnDate", title: "退帳日期", className: "excel_export"},
                        {data: "requisitionType.name", "defaultContent": "n/a", title: "料號狀態", className: "excel_export"},
                        {data: "remark", "defaultContent": "n/a", title: "備註", className: "excel_export"},
                        {data: "id", "defaultContent": "n/a", title: "紀錄"},
                        {data: "lackingFlag", "defaultContent": "N", title: "已掛缺", className: "excel_export"},
                        {data: "isUrgent", "defaultContent": "", title: "急件", className: "excel_export"},
                        {data: "returnOrderNo", "defaultContent": "", title: "退料單號", className: "excel_export"}
                    ],
                    "columnDefs": [
                        {
                            "targets": [0],
                            "visible": false,
                            "searchable": false
                        },
                        {
                            "targets": [cateMesColumnIndex],
                            'render': function (data, type, full, meta) {
                                return data ? data :
                                        full.requisitionCateMesCustom ? full.requisitionCateMesCustom : "n/a";
                            }
                        },
                        {
                            "targets": [eventColumnIndex - 5, eventColumnIndex - 4, eventColumnIndex - 3],
                            "searchable": false,
                            'render': function (data, type, full, meta) {
                                return data == null ? "n/a" : formatDate(data);
                            }
                        },
                        {
                            "targets": [eventColumnIndex - 1],
                            "visible": isEditor || isInsertWh,
                            "searchable": isEditor || isInsertWh ? true : false
                        },
                        {
                            "targets": [eventColumnIndex],
                            "searchable": false,
                            'render': function (data, type, full, meta) {
                                return "<a href='event.jsp?requisition_id=" + data + "' target='_blank'>紀錄</a>";
                            }
                        },
                        {
                            "targets": [eventColumnIndex + 1],
                            "searchable": false,
                            'render': function (data, type, full, meta) {
                                return data == 1 ? "Y" : "N";
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
                    "lengthMenu": lengthList,
                    "filter": true,
                    "info": true,
                    "paginate": true,
                    "select": true,
                    "searchDelay": 1000,
                    "order": [[0, "desc"]]
                };

                const addBtn = {
                    "text": '新增需求',
                    init: function (dt, node, config) {
//                        $(node).toggle(!isM8User);
                    },
                    "attr": {
                        "data-toggle": "modal",
                        "data-target": "#myModal2",
                        "class": "dt-button crudBtn"
                    },
                    "action": function (e, dt, node, config) {
                        $("#model-table input").val("");
                        $("#model-table #id").val(0);

                        $("#model-table2 #floor\\.id  option[value='" + userFloorId + "']").prop('selected', true); // never empty even if option no match.
                    }
                };

                const insertWhBtn = {
                    "text": '開單領料',
                    "attr": {
                        "class": "dt-button crudBtn"
                    },
                    "action": function (e, dt, node, config) {
                        var cnt = table.rows('.selected').count();
                        if (cnt < 1)
                            return alert("Please select a row at least.");

                        if (!confirm(cnt + " rows selected. OK?"))
                            return;

                        var datas = table.rows('.selected').data().toArray();
                        if (isInsertWh && !isEditor) {
                            datas = datas.filter((item) => item.user.unit.id === userUnitId);
                            if (datas.length < 1)
                                return  alert("Not same team item.");
                        }
                        datas = datas.filter((item) => item.requisitionState.id === 4);
                        if (datas.length < 1) {
                            return  alert("待領料數量0.");
                        }

                        eFlow({
                            "datas": JSON.stringify(datas),
                            "commitJobNo": "<c:out value="${user.jobnumber}" />"
                        });
                    }
                };

                const addReturnBtn = {
                    "text": '新增退料',
                    "attr": {
                        "data-toggle": "modal",
                        "data-target": "#myModal",
                        "class": "dt-button crudBtn"
                    },
                    "action": function (e, dt, node, config) {
                        const $myModal = $("#myModal");
                        $myModal.find(".modal-title").html("退料");
                        $myModal.find("#dialog-msg").html("");

                        const $modelTable = $("#model-table");

                        $modelTable.find("input, textarea").val("");
                        $modelTable.find("#id").val(0);

                        $modelTable.find("#requisitionState\\.id").val(7);
                        $modelTable.find("#isUrgent").val(0);

                        $modelTable.find("#requisitionReason\\.id").val(2).change();
                        $modelTable.find("#floor\\.id").val(userFloorId).change();
                        $modelTable.find("#requisitionCateIms\\.id").val(0).change();
                    }
                };

                const editBtn = {
                    "text": '編輯',
                    "attr": {
                        "class": "dt-button crudBtn"
                    },
                    "action": function (e, dt, node, config) {
//                                    if (isEditor) {
//                                        $("#model-table #po, #materialNumber, #amount").attr("disabled", true);
//                                    }

                        const $myModal = $("#myModal");
                        $myModal.find(".modal-title").html("編輯紀錄");
                        $myModal.find("#dialog-msg").html("");


                        var cnt = table.rows('.selected').count();
                        if (cnt !== 1) {
                            alert("Please select a row.");
                            return false;
                        }

                        $('#myModal').modal('show');
                        const arr = table.rows('.selected').data();
                        const data = arr[0];

                        const $modelTable = $("#model-table");

                        $modelTable.find("#requisitionState\\.id option[value='4']").toggle(data.requisitionState.id === 4);

                        $modelTable.find("#id").val(data.id);
                        $modelTable.find("#po").val(data.po);
                        $modelTable.find("#materialNumber").val(data.materialNumber);
                        $modelTable.find("#amount").val(data.amount);
                        $modelTable.find("#floor\\.id").val(data.floor.id);
                        $modelTable.find("#requisitionFlow\\.id").val(data['requisitionFlow'] && data.requisitionFlow != null ? data.requisitionFlow.id : 1);
                        $modelTable.find("#requisitionReason\\.id").val(data.requisitionReason.id);
                        $modelTable.find("#requisitionCateIms\\.id").val(data['requisitionCateIms'] && data.requisitionCateIms != null ? data.requisitionCateIms.id : 0);
                        $modelTable.find("#requisitionCateMes\\.id").val(data['requisitionCateMes'] && data.requisitionCateMes != null ? data.requisitionCateMes.id : 0);
                        $modelTable.find("#requisitionCateMesCustom").val(data.requisitionCateMesCustom);
                        $modelTable.find("#requisitionState\\.id").val(data.requisitionState.id);
                        $modelTable.find("#user\\.id").val(data.user.id);
                        $modelTable.find("#materialType").val(data.materialType);
                        $modelTable.find("#materialBoardSn").val(data.materialBoardSn);
                        $modelTable.find("#remark").val(data.remark);
                        $modelTable.find("#isUrgent option").filter(function () {
                            return $(this).text() === data.isUrgent;
                        }).prop('selected', true);
                        $modelTable.find("#returnOrderNo").val(data.returnOrderNo);

                        $modelTable.find("#requisitionReason\\.id").change();
                        $modelTable.find("#floor\\.id").change();
                        $modelTable.find("#requisitionCateIms\\.id").change();
                    }
                };

                const excelBtn = {
                    "extend": 'excel',
                    "exportOptions": {
//                        "columns": 'th:not(:first-child):not(:last-child)',
                        "columns": 'th.excel_export',
                        modifier: {
                            // DataTables core
                            search: 'applied',
                            order: 'applied'
                        }
                    }
                };

                const sapBtn = {
                    "text": 'SAP資訊',
                    "attr": {
                    },
                    "action": function (e, dt, node, config) {
                        const cnt = table.rows('.selected').count();
                        if (cnt != 1) {
                            alert("Please select a row.");
                            return false;
                        }

                        const arr = table.rows('.selected').data();
                        const data = arr[0];

                        const materialNumbers = [data.materialNumber, data.materialNumber];

                        retrieveSapInfo({
                            "po": data.po,
                            "materialNumbers": materialNumbers
                        });
                    }
                };

                if (!isEditor) {
                    if (isInsertWh) {
                        var extraSettingRepair = {
                            "dom": 'Bfrtip',
                            "buttons": [
                                'pageLength',
                                addBtn,
                                insertWhBtn
                            ]
                        };
                        $.extend(dataTable_config, extraSettingRepair);
                    } else if (${isUser}) {
                        var extraSetting = {
                            "dom": 'Bfrtip',
                            "buttons": [
                                'pageLength',
                                addBtn
                            ]
                        };
                        $.extend(dataTable_config, extraSetting);
                    }
                } else
                if (isM8Oper) {
                    var extraSetting2 = {
                        "dom": 'Bfrtip',
                        "buttons": [
                            'pageLength',
                            addReturnBtn,
                            editBtn,
                            excelBtn,
                            sapBtn
                        ]
                    };
                    $.extend(dataTable_config, extraSetting2);
                } else
                if (isEditor) {
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
                            addBtn,
                            addReturnBtn,
                            editBtn,
                            insertWhBtn,
                            {
                                "text": '轉不良缺',
                                "attr": {
                                    "class": "dt-button crudBtn"
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
                                    $('#myModal3').modal('show');
                                    var arr = table.rows('.selected').data();
                                    var data = arr[0];

                                    $("#myModal3 #model-table #requision_id").val(data.id);
                                    $("#model-table #itemses\\[0\\]\\.label1").val(data.po);
                                    $("#model-table #itemses\\[0\\]\\.label3").val(data.materialNumber);
                                    $("#model-table #itemses\\[0\\]\\.label4").val(data.materialBoardSn);
                                    $("#model-table #number").val(data.amount);
                                    $("#model-table #comment").val("申請人：" + data.user.username + "。" + data.remark);
                                    $("#model-table #email").val(data.user.email).attr("disabled", true);
                                }
                            },
                            excelBtn,
                            sapBtn
                        ]
                    };
                    $.extend(dataTable_config, extraSetting2);
                }

//                table = $('#favourable').DataTable(dataTable_config); //for search header
                $('#favourable thead tr').clone(false).appendTo('#favourable thead');
                $('#favourable thead tr:eq(1) th').each(function (i) {
                    var title = $(this).text();
                    $(this).html('<input type="text" id=_header_fitler_' + title + ' placeholder="Search ' + title + '" />');
                    $('input', this).on('change', function (e) {
                        e.preventDefault();

                        const thIndex = $(this).closest('th').index(); // 取得目前欄位的 index
                        const originalTh = $('#favourable thead tr:eq(0) th').eq(thIndex).get(0);
                        const columnIndex = table.columns().header().indexOf(originalTh);

                        if (columnIndex !== -1 && table.column(columnIndex).search() !== this.value) {
                            table
                                    .column(columnIndex)
                                    .search(this.value)
                                    .draw();
                        }
                    });
                });

//                $("#_header_fitler_日期").addClass("search_disabled").attr("disabled", true);

                table = $('#favourable').DataTable(dataTable_config);

                table.on('length.dt', function (e, settings, len) {
                    checkPageLenAll(len);
                });

                table.on('preDraw.dt', function (e, settings) {
                    checkPageLenAll(table.page.len());
                });

                function checkPageLenAll(len) {
                    if (len === -1) {
                        if ($("#datepicker_from").val() === "" || $("#datepicker_to").val() === "") {
                            alert("Fill date interval first to show all rows.");
                            table.page.len(10).draw();
                            wsOpen();
                        } else {
                            disconnectToServer();
                        }
                    } else {
                        wsOpen();
                    }
                }

                $("#datepicker_from, #datepicker_to, #respectDate").datepicker({
                    format: "yyyy-mm-dd",
                    showOn: "button",
                    buttonImage: "images/calendar.gif",
                    buttonImageOnly: false
                }).on('change', function () {
                    $('.datepicker').hide();
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

                $(".hide_col").hide();

                function formatDate(ds) {
                    return moment.utc(ds) // October 22nd 2018, 10:37:08 am
                            .format('YY/MM/DD HH:mm');
                }

                enterToTab();

                $(":text:not(.noUpperCase)").keyup(function () {
                    textBoxToUpperCase($(this));
                }).focus(function () {
                    $(this).select();
                });

                //auto uppercase the textbox value(PO, ModelName)
                function textBoxToUpperCase(obj) {
                    obj.val(obj.val().trim().toLocaleUpperCase());
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

                function retrieveSapInfo(data) {
                    const target = $("#sap-material-info");
                    target.html("<h5>Please wait...</h5>");
                    $.ajax({
                        type: "POST",
                        url: "<c:url value="/RequisitionController/retrieveSapInfos" />",
                        dataType: "json",
                        data: data,
                        success: function (response) {
                            const arr = response;
                            if (arr.length <= 0) {
                                target.html("<h5>No data</h5>");
                                return;
                            }
                            const {materialNumber, amount, unitPrice, storageSpaces, poQty} = arr[0];
                            var realPoQty = isNullOrZero(poQty) ? -1 : poQty;
                            var unitAmount = (Number(amount) / Number(realPoQty)).toFixed(1);
                            target.html('<h5>料號: ' + materialNumber +
                                    ' 發料量: ' + amount +
                                    ' 單價: ' + unitPrice +
                                    ' 儲區: ' + storageSpaces +
                                    ' 單台用量: ' + unitAmount + '</h5>');

                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            target.html(xhr.responseText);
                        }
                    });
                }

                function eFlow(data) {
                    $.ajax({
                        type: "POST",
                        url: "<c:url value="/RequisitionController/insertEflow" />",
                        dataType: "json",
                        data: data,
                        success: function (response) {
//                            //enduser use IE not support ws
                            refreshTable();
                            ws.send("UPDATE");
                            $.notify('資料已更新', {placement: {
                                    from: "bottom",
                                    align: "right"
                                }
                            });
                            return alert(response);
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    });
                }

                //Websocket connect part
                var hostname = window.location.host;//Get the host ip address to link to the server.

                var wsFailMsg = $("#ws-connect-fail-message");
                function connectToServer() {

                    try {
                        ws = new WebSocket("ws://" + hostname + "${pageContext.request.contextPath}/myHandler");

                        ws.onopen = function () {
                            wsFailMsg.remove();
                            console.log("Connected");
                        };
                        ws.onmessage = function (event) {
                            var d = event.data;
                            d = d.replace(/\"/g, "");
                            console.log(d);
                            if (("ADD" == d || "REMOVE" == d || "UPDATE" == d)) {
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
                    if (isWebSocketOpen()) {
                        ws.close();
                        table.buttons('.crudBtn').disable();
                    }
                }
                function wsOpen() {
                    if (isWebSocketClose()) {
                        connectToServer();
                        table.buttons('.crudBtn').enable();
                    }
                }

                if (ws != null) {
                }

                function isWebSocketClose() {
                    return ws && (ws.readyState === WebSocket.CLOSED || ws.readyState === WebSocket.CLOSING);
                }

                function isWebSocketOpen() {
                    return ws && (ws.readyState === WebSocket.OPEN);
                }

            });

            function refreshTable() {
                table.ajax.reload(null, false);
            }

            function isNullOrZero(testString) {
                return testString == null || testString == 0;
            }

            function getModalTitle($sel) {
                return $sel.closest('td').prev('td').text();
            }
        </script>        
        <script>
            const mtmMap = new Map();

            function initManyToManyMap() {

                const requestParams = [
                    {
                        url: "<c:url value="/RequisitionController/findRequisitionCateImsRef" />",
                        attribute: "floors",
                        kvMapName: "floorToCateImsMap"
                    },
                    {
                        url: "<c:url value="/RequisitionController/findRequisitionCateMesRef" />",
                        attribute: "requisitionCateImss",
                        kvMapName: "imsToMesMap"
                    }
                ];

                requestParams.forEach(params => {
                    fetch(params.url)
                            .then(res => res.json())
                            .then(data => {
                                const resultMap = new Map();
                                const refMap = new Map();

                                data.forEach(item => {
                                    const targetList = item[params.attribute] || [];

                                    targetList.forEach(tar => {
                                        let tarId;

                                        if (tar['@id']) {
                                            refMap.set(tar['@id'], tar);
                                        }

                                        if (tar['@ref']) {
                                            const resolved = refMap.get(tar['@ref']);
                                            if (resolved)
                                                tarId = resolved.id;
                                        } else if (tar.id) {
                                            tarId = tar.id;
                                        }

                                        if (tarId !== undefined) {
                                            if (!resultMap.has(tarId)) {
                                                resultMap.set(tarId, []);
                                            }
                                            resultMap.get(tarId).push(item);
                                        }
                                    });
                                });

                                mtmMap.set(params.kvMapName, resultMap);
                            });
                });
            }

            function initDropDownOptionAndEvent() {
                initDropDownOption();
                setSelectorEvent();
            }

            function initDropDownOption() {
                const requestParams = [
                    {
                        url: "<c:url value="/RequisitionController/findRequisitionReasonOptions" />",
                        target: $("#model-table #requisitionReason\\.id"),
                        func: [
                            {
                                ptr: adjustRequisitionReason,
                                funcTargets: [
                                    {sel: $("#model-table #requisitionReason\\.id"), optionVal: 2}
                                ]
                            }
                        ]
                    },
                    {
                        url: "<c:url value="/RequisitionController/findRequisitionStateOptions" />",
                        target: $("#model-table #requisitionState\\.id")
                    },
                    {
                        url: "<c:url value="/RequisitionController/findRequisitionTypeOptions" />",
                        target: $("#model-table #requisitionType\\.id")
                    },
                    {
                        url: "<c:url value="/OrdersController/findOrderTypesOptions" />",
                        target: $("#model-table #orderTypes\\.id")
                    },
                    {
                        url: "<c:url value="/RequisitionController/findFloorOptions" />",
                        target: $("#floor\\.id"),
                        func: [
                            {
                                ptr: defaultFirstOption,
                                funcTargets: [
                                    {sel: $("#model-table2 #floor\\.id"), optionVal: 9}
                                ]
                            }
                        ]
                    },
                    {
                        url: "<c:url value="/RequisitionController/findRequisitionFlowOptions" />",
                        target: $("#requisitionFlow\\.id")
                    },
                    {
                        url: "<c:url value="/RequisitionController/findRequisitionCateImsOptions" />",
                        target: $("#model-table #requisitionCateIms\\.id")
                    },
                    {
                        url: "<c:url value="/RequisitionController/findRequisitionCateMesOptions" />",
                        target: $("#model-table #requisitionCateMes\\.id")
                    },
                    {
                        url: "<c:url value="/RequisitionController/findUrgentOptions" />",
                        target: $("#model-table #isUrgent")
                    }
                ];

                requestParams.map(params => {
                    $.ajax({
                        type: "GET",
                        url: params.url,
                        success: function (response) {
                            var sel = params.target;
                            var d = response;
                            for (var i = 0; i < d.length; i++) {
                                var options = d[i];
                                sel.append("<option value='" + options.id + "'>" + options.name + "</option>");
                            }

                            if (params.func) {
                                const funcs = params.func;
                                for (var j = 0; j < funcs.length; j++) {
                                    const method = funcs[j].ptr;
                                    if (funcs[j].funcTargets) {
                                        const funcTargets = funcs[j].funcTargets;
                                        for (var k = 0; k < funcTargets.length; k++) {
                                            method(funcTargets[k]);
                                        }
                                    } else {
                                        method();
                                    }
                                }
                            }
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    });
                });
            }

            function setSelectorEvent() {
                initManyToManyMap();

                const eventParams = [
                    {
                        ptr: requisitionReasonEvent,
                        funcTargets: [
                            {
                                sel: $("#model-table #requisitionReason\\.id"),
                                selTar: $("#model-table #requisitionType\\.id")
                            }
                        ]
                    },
                    {
                        ptr: selectorManytomanyEvent,
                        funcTargets: [
                            {
                                sel: $("#model-table #floor\\.id"),
                                kvMapName: "floorToCateImsMap",
                                selTar: $("#model-table #requisitionCateIms\\.id")
                            },
                            {
                                sel: $("#model-table #requisitionCateIms\\.id"),
                                kvMapName: "imsToMesMap",
                                selTar: $("#model-table #requisitionCateMes\\.id")
                            }
                        ]
                    },
                    {
                        ptr: myModalFloorAndTypeEvent,
                        funcTargets: [
                            {
                                sel: $("#model-table")
                            }
                        ]
                    }
                ];

                eventParams.map(params => {
                    const method = params.ptr;
                    if (params.funcTargets) {
                        const funcTargets = params.funcTargets;
                        for (var k = 0; k < funcTargets.length; k++) {
                            method(funcTargets[k]);
                        }
                    } else {
                        method();
                    }
                });
            }

            function adjustRequisitionReason(funcTarget) {
                defaultFirstOption(funcTarget);
                const $selector = funcTarget.sel;

                const order = ['2', '5', '6', '3', '7'];
                const sortedOptions = $selector.find("option").sort((a, b) => { // a is the latter one.
                    const idxA = order.indexOf(a.value);
                    const idxB = order.indexOf(b.value);

                    // Values not in the specified order are sorted last.
                    const posA = idxA === -1 ? Infinity : idxA;
                    const posB = idxB === -1 ? Infinity : idxB;

                    return posA - posB;
                });
                $selector.empty().append(sortedOptions);

                $selector.find("option").each(function (index) {
                    const letter = String.fromCharCode(65 + index); // A is 65 in ASCII, B is 66, and so on
                    $(this).text(letter + ". " + $(this).text());
                });
            }

            function defaultFirstOption(funcTarget) {
                var $selector = funcTarget.sel;
                var optionVal = funcTarget.optionVal;

                const targetOption = $selector.find("option[value='" + optionVal + "']").prop('selected', true);
                $selector.prepend(targetOption);
            }

            function requisitionReasonEvent(funcTarget) {
                const $srcSelector = funcTarget.sel;
                const $selTar = funcTarget.selTar;
                $srcSelector.on('change', function () {
                    const selectedValue = parseInt($(this).val());

                    var typeId;
                    switch (selectedValue) {
                        case 6:
                            typeId = 3;
                            break;
                        case 3:
                        case 7:
                            typeId = 1;
                            break;
                        case 2:
                        case 5:
                            typeId = 2;
                            break;
                        default:
                            typeId = 1;
                    }

                    $selTar.val(typeId).change();
                });
            }

            function selectorManytomanyEvent(funcTarget) {
                const $srcSelector = funcTarget.sel;
                const kvMapName = funcTarget.kvMapName;
                const $selTar = funcTarget.selTar;

                $srcSelector.on('change', function () {
                    const selectedValue = parseInt($(this).val());
                    const kvMap = mtmMap.get(kvMapName);

                    const d = kvMap.get(selectedValue);

                    const $selectorTar = $selTar;
                    const selectedTarValue = $selectorTar.val();
                    $selectorTar.empty();
                    for (var i = 0; d != null && i < d.length; i++) {
                        var options = d[i];
                        $selectorTar.append("<option value='" + options.id + "'>" + options.name + "</option>");
                    }
                    $selectorTar.val(selectedTarValue);
                });
            }

            function myModalFloorAndTypeEvent(funcTarget) {
                const $modal = funcTarget.sel;

                const $sel = $modal.find("#floor\\.id");
                const $selType = $modal.find("#requisitionType\\.id");

                const $selIms = $modal.find("#requisitionCateIms\\.id");
                const $selMesCustom = $modal.find("#requisitionCateMesCustom");
                const $selMes = $modal.find("#requisitionCateMes\\.id");

                const $selImsTr = $selIms.closest('tr');
                const $selMesCustomTr = $selMesCustom.closest('tr');
                const $selMesTr = $selMes.closest('tr');
                const $selFlowTr = $modal.find("#requisitionFlow\\.id").closest('tr');

                $sel.add($selType).on('change', function () {
                    let isM8 = $sel.val() === '6';
                    if (isM8) {
                        $selMes.val(0);
                    } else {
                        $selMesCustom.val('');
                    }

                    let isNotGood = $selType.val() === '2';
                    if (!isNotGood) {
                        $selIms.val(0);
                        $selMes.val(0);
                        $selMesCustom.val('');
                    }

                    $selFlowTr.toggle(!isM8);
                    $selImsTr.toggle(isNotGood);
                    $selMesCustomTr.toggle(isM8 && isNotGood);
                    $selMesTr.toggle(!isM8 && isNotGood);
                });
            }
        </script>
        <script>
            $(function () {
                $("#myModal #save").click(function () {
                    let allValid = validSelect($("#myModal"));
                    if (!allValid) {
                        return alert("下拉選單 can't be empty.");
                    }

                    const $modelTable = $("#model-table");

                    const $amount = $modelTable.find("#amount");
                    const amount = $amount.val();
                    if (isNaN(amount) || amount === "") {
                        return alert(getModalTitle($amount) + " please insert a number.");
                    }

                    let alertColumns = [];
                    $modelTable.find('#po, #materialNumber, #requisitionCateMesCustom:visible')
                            .each(function () {
                                if ($(this).val().trim() === "") {
                                    allValid = false;
                                    alertColumns.push(getModalTitle($(this)));
                                }
                            });
                    if (!allValid) {
                        return alert(alertColumns + " can't be empty.");
                    }

                    if (confirm("Confirm save?")) {
                        let data = {};
                        if ($('#myModal').find('#id').val() > 0) {
                            const arr = table.rows('.selected').data();
                            data = arr[0];
                        }

                        const valIms = $modelTable.find("#requisitionCateIms\\.id").val();
                        const valMes = $modelTable.find("#requisitionCateMes\\.id").val();

                        var dataInput = {
                            po: $modelTable.find("#po").val(),
                            materialNumber: $modelTable.find("#materialNumber").val(),
                            amount: amount,
                            floor: {id: $modelTable.find("#floor\\.id").val()},
                            requisitionFlow: {id: $modelTable.find("#requisitionFlow\\.id").val()},
                            requisitionReason: {id: $modelTable.find("#requisitionReason\\.id").val()},
                            requisitionCateIms: isNullOrZero(valIms) ? {} : {id: valIms},
                            requisitionCateMes: isNullOrZero(valMes) ? {} : {id: valMes},
                            requisitionCateMesCustom: $modelTable.find("#requisitionCateMesCustom").val(),
                            requisitionState: {id: $modelTable.find("#requisitionState\\.id").val()},
                            requisitionType: {id: $modelTable.find("#requisitionType\\.id").val()},
                            materialType: $modelTable.find("#materialType").val(),
                            materialBoardSn: $modelTable.find("#materialBoardSn").val(),
                            remark: $modelTable.find("#remark").val(),
                            isUrgent: $modelTable.find("#isUrgent option:selected").text(),
                            returnOrderNo: $modelTable.find("#returnOrderNo").val()
                        };
                        if (dataInput.id == 0) {
                            delete dataInput["user.id"];
                        }

                        $.extend(data, dataInput);
                        save(data);
                    }
                });

                $("#myModal2 #save").click(function () {
                    let allValid = validSelect($("#myModal2"));
                    if (!allValid) {
                        return alert("下拉選單 can't be empty.");
                    }

                    if (confirm("Confirm save?")) {
                        const tb = $("#material-detail tbody tr");
                        const po = $("#model-table2 #po").val();
                        const floor = $("#model-table2 #floor\\.id").val();

                        const myArray = tb.map(function () {
                            var amount = $(this).find("input").eq(1).val();
                            const o = {
                                po: po,
                                materialNumber: $(this).find("input").eq(0).val(),
                                amount: amount === "" ? 0 : amount,
                                remark: $(this).find("#remark").val(),
                                "floor.id": floor,
                                "requisitionFlow.id": $(this).find("#requisitionFlow\\.id").val()
                            };
                            return o;
                        }).get();

                        const data = {
                            "myList": myArray
                        };
                        batchSave(data, function () {
                            $("#myModal2 input, textarea").val("");
                            $("#myModal2 select").prop('selectedIndex', 0);
                            $("#myModal2 #material-detail").find("tbody>tr").not(":eq(0)").detach();
                        });
                    }
                });

                $("#myModal3 #save").click(function () {
                    if (confirm("資料轉不良缺?")) {
                        var requision_id = $("#myModal3 #model-table #requision_id").val();
                        var number = $("#myModal3 #model-table #number").val();
                        var po = $("#myModal3 #model-table #itemses\\[0\\]\\.label1").val();
                        var material = $("#myModal3 #model-table #itemses\\[0\\]\\.label3").val();
                        var snBoard = $("#myModal3 #model-table #itemses\\[0\\]\\.label4").val();
                        var orderType = $("#myModal3 #model-table #orderTypes\\.id").val();
                        var respectDate = $("#myModal3 #model-table #respectDate").val();
                        var comment = $("#myModal3 #model-table #comment").val();

                        if (isNaN(number) || number == "") {
                            alert("Amount please insert a number.");
                            return false;
                        }
                        if (po == "" || material == "") {
                            alert("Po or MaterialNumber can't be empty.");
                            return false;
                        }
                        if (respectDate == "") {
                            const title = getModalTitle($("#myModal3 #model-table #respectDate"));
                            return alert(title + " can't be empty.");
                        }
                        var data = {
                            "requision_id": requision_id,
                            "po": po,
                            "material": material,
                            "snBoard": snBoard,
                            "number": number,
                            "orderTypes.id": orderType,
                            "respectDate": respectDate,
                            "comment": comment
                        };

                        saveToOrders(data);
                    }
                });

                $("body").on("keyup", "#po, #materialNumber", function () {
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

                $("#myModal2").find("input, select, textarea").addClass("form-control");

            });

            function save(data) {
                $.ajax({
                    type: "POST",
                    url: "<c:url value="/RequisitionController/save" />",
                    data: data,
                    success: function (response) {
                        $('#myModal').modal('toggle');
                        refreshTable();
                        ws.send("UPDATE");
                        $.notify('資料已更新', {placement: {
                                from: "bottom",
                                align: "right"
                            }
                        });
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        $("#dialog-msg").html(xhr.responseText);
                    }
                });
            }

            function batchSave(data, fn) {
                $.ajax({
                    type: "POST",
                    url: "<c:url value="/RequisitionController/batchSave" />",
                    dataType: "json",
                    data: data,
                    success: function (response) {
                        $('#myModal2').modal('toggle');
                        refreshTable();
                        ws.send("ADD");
                        $.notify('資料已更新', {placement: {
                                from: "bottom",
                                align: "right"
                            }
                        });

                        if (fn != null) {
                            fn();
                        }
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        $("#dialog-msg2").html(xhr.responseText);
                    }
                });
            }

            function saveToOrders(data) {
                $.ajax({
                    type: "POST",
                    url: "<c:url value="/OrdersController/save" />",
                    dataType: "html",
                    data: data,
                    success: function (response) {
                        $('#myModal3').modal('toggle');
                        refreshTable();
                        ws.send("UPDATE");
                        $.notify('資料已更新', {placement: {
                                from: "bottom",
                                align: "right"
                            }
                        });

                        $('#myModal3 :text').val("");
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        $("#dialog-msg3").html(xhr.responseText);
                    }
                });
            }

            function validSelect($modal) {
                let allValid = true;
                $modal.find('select:visible').each(function () {
                    if (!$(this).val()) {
                        allValid = false;
                        return false;
                    }
                });
                return allValid;
            }

        </script>
    </head>
    <body>
        <!-- Modal -->
        <div id="myModal" class="modal fade" role="dialog">
            <div class="modal-dialog">

                <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 id="titleMessage" class="modal-title">編輯紀錄</h4>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
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
                                    <td class="lab">工單號碼</td>
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
                                <tr>
                                    <td class="lab">位置</td>
                                    <td>
                                        <select id="floor.id"></select>
                                    </td>
                                </tr>

                                <c:if test="${isUser && (!isOper || !isAdmin)}">
                                    <tr>
                                        <td class="lab">製程</td>
                                        <td>
                                            <select id="requisitionFlow.id"></select>
                                        </td>
                                    </tr>
                                </c:if>

                                <c:if test="${isOper || isAdmin || isOperM8}">
                                    <tr>
                                        <td class="lab">製程</td>
                                        <td>
                                            <select id="requisitionFlow.id"></select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">產線判定</td>
                                        <td>
                                            <select id="requisitionReason.id"></select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">材料類別</td>
                                        <td>
                                            <select id="requisitionCateIms.id"></select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">異常分類</td>
                                        <td>
                                            <select id="requisitionCateMes.id"></select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">異常分類</td>
                                        <td>
                                            <textarea id="requisitionCateMesCustom"></textarea>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">不良原因</td>
                                        <td>
                                            <textarea id="materialType"></textarea>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">材料序號</td>
                                        <td>
                                            <input type="text" id="materialBoardSn"/>
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
                                    <tr class="hide_col">
                                        <td class="lab">料號狀態</td>
                                        <td>
                                            <select id="requisitionType.id"></select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">急件</td>
                                        <td>
                                            <select id="isUrgent"></select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">退料單號</td>
                                        <td>
                                            <input type="text" id="returnOrderNo"/>
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
                                    <td class="lab">工單號碼</td>
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
                                                    <th width="25%">料號</th>
                                                    <th>數量</th>
                                                    <th width="20%">製程</th>
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
                                                        <select id="requisitionFlow.id"></select>
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
                                <tr>
                                    <td class="lab">位置</td>
                                    <td> 
                                        <select id="floor.id"></select>
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

        <!-- Modal -->
        <div id="myModal3" class="modal fade" role="dialog">
            <div class="modal-dialog modal-lg">

                <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 id="titleMessage3" class="modal-title">轉不良缺</h4>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div>
                            <table id="model-table" cellspacing="10" class="table table-bordered">
                                <tr class="hide_col">
                                    <td class="lab">id</td>
                                    <td>
                                        <input type="text" id="requision_id" disabled="true" readonly="readonly">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">工單號碼</td>
                                    <td> 
                                        <input type="text" id="itemses[0].label1" readonly="readonly">
                                    </td>
                                </tr>
                                <tr class="hide_col">
                                    <td class="lab">機種</td>
                                    <td>
                                        <input type="text" id="itemses[0].label2" readonly="readonly" />
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">料號</td>
                                    <td>
                                        <input type="text" id="itemses[0].label3" readonly="readonly" />
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">材料序號</td>
                                    <td>
                                        <input type="text" id="itemses[0].label4" />
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">數量</td>
                                    <td>
                                        <input type="number" id="number">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">類型</td>
                                    <td>
                                        <select id="orderTypes.id"></select>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="lab">期望入料日</td>
                                    <td>
                                        <input type="text" id="respectDate">
                                    </td>
                                </tr>

                                <tr>
                                    <td class="lab">不良敘述</td>
                                    <td>
                                        <textarea id="comment"></textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="lab">申請人Email</td>
                                    <td>
                                        <input type="text" class="noUpperCase" id="email" readonly="readonly">
                                    </td>
                                </tr>
                            </table>
                            <div id="dialog-msg3" class="alarm"></div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" id="save" class="btn btn-default btn-outline-dark">Save</button>
                        <button type="button" class="btn btn-default btn-outline-dark" data-dismiss="modal">Close</button>
                    </div>
                </div>

            </div>
        </div>

        <div class="container-fluid box">
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
                                <span id="date-label-from" class="date-label">From: </span>
                                <input class="date_range_filter date form-control" type="text" id="datepicker_from" placeholder="請選擇起始時間" />
                                <span id="date-label-to" class="date-label">To: </span>
                                <input class="date_range_filter date form-control" type="text" id="datepicker_to"  placeholder="請選擇結束時間"/>
                                <input type="button" id="search" class="form-control" value="搜尋" />
                                <input type="button" id="clear" class="form-control" value="清除搜尋" />
                                <div id="sap-material-info"></div>
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
