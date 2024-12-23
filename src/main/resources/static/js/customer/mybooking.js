let t, a, n;
var e, s = $(".myBookingTable"), o = $(".select2"), i = "/admin/user-management/user-detail", r = {
    1: {title: "Pending deposit", class: "bg-label-warning"},
    2: {title: "Confirmed", class: "bg-label-success"},
    3: {title: "In-progress", class: "bg-label-danger"},
    4: {title: "Pending payment", class: "bg-label-secondary"},
    5: {title: "Completed", class: "bg-label-secondary"},
    6: {title: "Cancelled", class: "bg-label-secondary"},
    7: {title: "Pending Cancel", class: "bg-label-secondary"},
    8: {title: "Pending Return", class: "bg-label-secondary"},
};
o.length && (o = o).wrap('<div class="position-relative"></div>').select2({
    placeholder: "Select Country",
    dropdownParent: o.parent()
}),
s.length && (e = s.DataTable({
    ajax: {
        url: "/api/my-booking",
        method: "GET",
        dataSrc: ''
    },
    columns: [
        {data: "bookingId"},
        {data: "bookingId"},
        {data: "car.brand.brandName"},
        {data: "startDate"},
        {data: "endDate"},
        {data: "totalPrice"},
        {data: "car.brand.brandName"},
        {data: "bookingStatus.bookingStatusId"},
        {data: "action"},
    ],
    columnDefs: [{
        className: "control",
        searchable: !1,
        orderable: !1,
        responsivePriority: 1,
        targets: 0,
        render: function (e, t, a, n) {
            return ""
        }
    }, {
        targets: 2,
        responsivePriority: 1,
        render: function (e, t, a, n) {
            var s = a.car.carName,
                o = a.car.brand.brandName,
                r = a.car.frontImage == null ? null : a.car.frontImage;

            return '<div class="d-flex justify-content-start align-items-center user-name flex-wrap" data-brand="' + o + '">' +
                '<div class="avatar-wrapper">' +
                '<div class="avatar avatar-sm me-4">' +
                (r
                    ? '<img src="' + "\\" + r + '" alt="Avatar" style="width: 200px; height: 130px; object-fit: cover; margin-right: 10px; border-radius: 10px">'
                    : '<span class="avatar-initial rounded-circle bg-label-' +
                    ["success", "danger", "warning", "info", "dark", "primary", "secondary"][Math.floor(6 * Math.random())] +
                    '">' + (s.charAt(0).toUpperCase()) + "</span>") +
                '</div>' +
                '</div>' +
                '<div class="d-flex flex-column">' +
                '<a href="' + i + '?userId=' + a.userId + '" class="text-heading text-truncate">' +
                '<span class="fw-medium">' + s + '</span>' +
                '</a>' +
                '<small>' + o + '</small>' +
                '</div>' +
                '</div>';
        }
    }, {
        targets: 3,
        responsivePriority: 100,
        render: function (e, t, a, n) {
            a = new Date(a.startDate);
            const startDate = a.getDate() + "-" + (a.getMonth() + 1) + "-" + a.getFullYear();
            return '<span class="text-heading">' + startDate + "</span>"
        }
    }, {
        targets: 4,
        responsivePriority: 100,
        render: function (e, t, a, n) {
            a = new Date(a.endDate);
            const endDate = a.getDate() + "-" + (a.getMonth() + 1) + "-" + a.getFullYear();
            return '<span class="text-heading">' + endDate + "</span>"
        }
    }, {
      targets: 6,
        visible: false,
        searchable: true,
    }, {
        targets: 7,
        responsivePriority: 2,
        render: function (e, t, a, n) {
            a = a.bookingStatus.bookingStatusId;
            return '<span class="badge ' + r[a].class + '" text-capitalized>' + r[a].title + "</span>"
        }
    },
        {
        targets: -1,
        title: "Actions",
        searchable: !1,
        orderable: !1,
        render: function (e, t, a, n) {
            const bookingStatusId = a.bookingStatus.bookingStatusId;
            let button = '';
            if (bookingStatusId === 1 || bookingStatusId === 2) {
                button = '<a class="btn bg-label-danger">Cancel</a>';
            }
            return '<div class="d-flex align-items-center" style="gap: 10px">'+button+'<a href="" class="btn bg-label-success"></i>Details</a></div></div>'
        }
    }],
    order: [[2, "desc"]],
    dom: '<"row"<"col-md-2"<"ms-n2"l>><"col-md-10"<"dt-action-buttons text-xl-end text-lg-start text-md-end text-start d-flex align-items-center justify-content-end flex-md-row flex-column mb-6 mb-md-0 mt-n6 mt-md-0 gap-md-4"fB>>>t<"row"<"col-sm-12 col-md-6"i><"col-sm-12 col-md-6"p>>',
    language: {
        sLengthMenu: "_MENU_",
        search: "",
        searchPlaceholder: "Search Booking",
        paginate: {
            next: '<i class="bx bx-chevron-right bx-18px"></i>',
            previous: '<i class="bx bx-chevron-left bx-18px"></i>'
        }
    },
    responsive: {
        details: {
            display: $.fn.dataTable.Responsive.display.modal({
                header: function (e) {
                    return "Details of " + e.data().username
                }
            }), type: "column", renderer: function (e, t, a) {
                a = $.map(a, function (e, t) {
                    return "" !== e.title ? '<tr data-dt-row="' + e.rowIndex + '" data-dt-column="' + e.columnIndex + '"><td>' + e.title + ":</td> <td>" + e.data + "</td></tr>" : ""
                }).join("");
                return !!a && $('<table class="table"/><tbody />').append(a)
            }
        }
    },
    initComplete: function () {
        this.api().columns(6).every(function () {
            var t = this,
                a = $('<select id="UserRole" class="form-select text-capitalize" style="width: 100%;\n' +
                    '    border-radius: 10px;\n' +
                    '    border: 3px solid #efefef;\n' +
                    '    padding: 10px 10px;\n' +
                    '}"><option value=""> Select Brand </option></select>').appendTo(".car_brand").on("change", function () {
                    var e = escapeRegex($(this).val());
                    t.search(e ? '^' + e + '$' : '', true, false).draw();
                });
            t.data().unique().sort().each(function (e, t) {
                a.append('<option value="' + e + '">' + e + "</option>")
            })
        }),
            //     this.api().columns(4).every(function () {
            //     var t = this,
            //         a = $('<select id="UserPlan" class="form-select text-capitalize"><option value=""> Select Plan </option></select>').appendTo(".user_plan").on("change", function () {
            //             var e = $.fn.dataTable.util.escapeRegex($(this).val());
            //             t.search(e ? "^" + e + "$" : "", !0, !1).draw()
            //         });
            //     t.data().unique().sort().each(function (e, t) {
            //         a.append('<option value="' + e + '">' + e + "</option>")
            //     })
            // }),
            this.api().columns(7).every(function () {
                var t = this,
                    a = $('<select id="FilterTransaction" class="form-select text-capitalize" style="width: 100%;\n' +
                        '    border-radius: 10px;\n' +
                        '    border: 3px solid #efefef;\n' +
                        '    padding: 10px 10px;\n' +
                        '}"><option value=""> Select Status </option></select>').appendTo(".booking_status").on("change", function () {
                        var e = escapeRegex($(this).val());
                        t.search(e ? "^" + e + "$" : "", !0, !1).draw()
                    });
                t.data().unique().sort().each(function (e, t) {
                    a.append('<option value="' + r[e].title + '" class="text-capitalize">' + r[e].title + "</option>")
                })
            })
    }
}),
    $(".dt-buttons > .btn-group > button").removeClass("btn-secondary")), $(".datatables-users tbody").on("click", ".delete-record", function () {
    e.row($(this).parents("tr")).remove().draw()
})

    setTimeout(() => {
        $(".dataTables_filter .form-control").removeClass("form-control-sm"), $(".dataTables_length .form-select").removeClass("form-select-sm")
    }, 300)


function escapeRegex(value) {
    return value.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');
}