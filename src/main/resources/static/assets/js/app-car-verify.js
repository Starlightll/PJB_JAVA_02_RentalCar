$(function () {
    var e, a = $(".dt-fixedheader");
    a.length && (a = a.DataTable({
        ajax: {
            url: "/carAPI/get-cars-by-status?statusId=8",
            method: "GET",
            dataSrc: '',
            error: function (xhr, status, error) {
                console.error("Error fetching data: ", error);
            }
        },
        columns: [
            {data: null}, // Column for control (expand/collapse)
            {data: "carId"},
            {data: "carId"},
            {data: "carName"},
            {data: "frontImage"},
            {data: "basePrice"},
            {
                data: "licensePlate",
            },
            {data: "carStatus.statusId"},
            {data: null},
        ],
        columnDefs: [{
            className: "control",
            orderable: !1,
            targets: 0,
            responsivePriority: 3,
            render: function (e, a, t, s) {
                return ""
            }
        }, {
            targets: 1,
            orderable: !1,
            render: function () {
                return '<input type="checkbox" class="dt-checkboxes form-check-input">'
            },
            checkboxes: {
                selectAllRender: '<input type="checkbox" class="form-check-input">'
            },
            responsivePriority: 4
        }, {
            targets: 2,
            visible: !1
        }, {
            targets: 3,
            render: function (e, a, t, s) {
                var l = t.frontImage;
                return '<img src="/' + l + '" class="w-100 rounded" style="height: 100px; object-fit: cover">'
            },
            responsivePriority: 5
        }, {
            responsivePriority: 1,
            targets: 4,
            render: function (e, a, t, s) {
                var l = t.frontImage
                    , r = t.carName
                    , d = t.brand.brandName;
                return '<div class="d-flex justify-content-start align-items-center"></div><div class="d-flex flex-column"><span class="emp_name text-truncate">' + r + '</span><small class="emp_post text-truncate text-muted">' + d + "</small></div></div>"
            },
        },{
            responsivePriority: 2,
            targets: 5,
            render: function (e, a, t, s) {
                return '<span class="">' + t.basePrice + " VND/Day</span>"
            }
        }, {
            responsivePriority: 2,
            targets: 6,
            render: function (e, a, t, s) {
                return '<span class="badge bg-light text-dark">' + t.licensePlate + "</span>"
            }
        }, {
            targets: -2,
            render: function (e, a, t, s) {
                var t = t.carStatus.statusId
                    , l = {
                    1: {title: "Current", class: "bg-label-primary"},
                    2: {title: "Professional", class: "bg-label-success"},
                    3: {title: "Rejected", class: "bg-label-danger"},
                    4: {title: "Resigned", class: "bg-label-warning"},
                    8: {title: "Verifying", class: "bg-label-warning"},
                };
                return void 0 === l[t] ? e : '<span class="badge ' + l[t].class + '">' + l[t].title + "</span>"
            }
        }, {
            targets: -1,
            title: "Actions",
            orderable: !1,
            render: function (e, a, t, s) {
                return '<div class="d-inline-block">' +
                    '<a onclick="verifyCar('+ t.carId +')" class="btn btn-icon item-check"><i class="bx bx-check bx-md" style="color: #7aff45"></i></a>' +
                    '<a href="javascript:;" class="btn btn-icon dropdown-toggle hide-arrow" data-bs-toggle="dropdown">' +
                    '<i class="bx bx-dots-vertical-rounded bx-md"></i>' +
                    '</a>' +
                    '<div class="dropdown-menu dropdown-menu-end m-0">' +
                    '<a href="javascript:;" class="dropdown-item">Details</a>' +
                    '<a href="javascript:;" class="dropdown-item">Archive</a>' +
                    '<div class="dropdown-divider"></div>' +
                    '<a href="javascript:;" class="dropdown-item text-danger delete-record">Delete</a>' +
                    '</div>' +
                    '</div>' +
                    '<a href="javascript:;" class="btn btn-icon item-edit"><i class="bx bx-edit bx-md"></i></a>'
            }
        }],
        order: [[2, "desc"]],
        dom: '<"row"<"col-sm-12 col-md-6"l><"col-sm-12 col-md-6 d-flex justify-content-center justify-content-md-end mt-n6 mt-md-0"f>>t<"row"<"col-sm-12 col-md-6"i><"col-sm-12 col-md-6"p>>',
        displayLength: 7,
        lengthMenu: [7, 10, 25, 50, 75, 100],
        language: {
            paginate: {
                next: '<i class="bx bx-chevron-right bx-18px"></i>',
                previous: '<i class="bx bx-chevron-left bx-18px"></i>'
            }
        },
        responsive: {
            details: {
                display: $.fn.dataTable.Responsive.display.modal({
                    header: function (e) {
                        return "Details of " + e.data().carName
                    }
                }),
                type: "column",
                renderer: function (e, a, t) {
                    t = $.map(t, function (e, a) {
                        return "" !== e.title ? '<tr data-dt-row="' + e.rowIndex + '" data-dt-column="' + e.columnIndex + '"><td>' + e.title + ":</td> <td>" + e.data + "</td></tr>" : ""
                    }).join("");
                    return !!t && $('<table class="table"/><tbody />').append(t)
                }
            }
        }
    }),
        window.Helpers.isNavbarFixed() ? (e = $("#layout-navbar").outerHeight(),
            new $.fn.dataTable.FixedHeader(a).headerOffset(e)) : new $.fn.dataTable.FixedHeader(a)),

        setTimeout(() => {
                $(".dataTables_filter .form-control").removeClass("form-control-sm"),
                    $(".dataTables_length .form-select").removeClass("form-select-sm")
            }
            , 200)
});

function verifyCar(carId) {
    Swal.fire({
        title: "Are you sure?",
        text: "You won't be able to revert this!",
        icon: "warning",
        showCancelButton: !0,
        confirmButtonText: "Yes, approved it!",
        customClass: {
            confirmButton: "btn btn-primary me-3",
            cancelButton: "btn btn-label-secondary"
        },
        buttonsStyling: !1
    }).then(function (e) {
        if (e.value) {
            $.ajax({
                url: `/carAPI/approve-car?carId=${carId}`,
                method: "GET",
                contentType: "application/json",
                success: function (response) {
                    Swal.fire({
                        icon: "success",
                        title: "Car approved successfully!",
                        showConfirmButton: !1,
                        timer: 1500,
                        customClass: {confirmButton: "btn btn-primary"},
                        buttonsStyling: !1
                    }),
                        $(".dt-fixedheader").DataTable().ajax.reload(null, !1)
                },
                error: function (e, a, t) {
                    console.error("Error confirming car: ", e);

                    let errorMessage = "An unknown error occurred";
                    if (e.responseJSON && e.responseJSON.error) {
                        errorMessage = e.responseJSON.error;
                    } else if (e.responseText) {
                        errorMessage = e.responseText;
                    }

                    Swal.fire({
                        icon: "question",
                        title: "Failed to approve the car",
                        text: errorMessage,
                        showConfirmButton: !1,
                        timer: 1500,
                        customClass: { confirmButton: "btn btn-primary" },
                        buttonsStyling: !1
                    });
                    $(".dt-fixedheader").DataTable().ajax.reload(null, !1)
                }
            })
        }
    })
}
