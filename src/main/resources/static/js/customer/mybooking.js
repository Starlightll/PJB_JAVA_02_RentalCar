

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
            {data: "bookingStatus.bookingStatusId"},
            {data: "action"}
        ],
        columnDefs: [{
            className: "control",
            searchable: !1,
            orderable: !1,
            responsivePriority: 2,
            targets: 0,
            render: function (e, t, a, n) {
                return ""
            }
        }, {
            targets: 2,
            responsivePriority: 4,
            render: function (e, t, a, n) {
                var s = a.car.carName, o = a.car.brand.brandName, r = a.car.frontImage == null ? null : a.car.frontImage;
                return '<div class="d-flex justify-content-start align-items-center user-name"><div class="avatar-wrapper"><div class="avatar avatar-sm me-4">' + (r ? '<img src="' + "\\" + r + '" alt="Avatar" class="" style="width: 200px; height: 130px; object-fit: cover; margin-right: 10px; border-radius: 10px">' : '<span class="avatar-initial rounded-circle bg-label-' + ["success", "danger", "warning", "info", "dark", "primary", "secondary"][Math.floor(6 * Math.random())] + '">' + (r = (((r = (s = a.username).match(/\b\w/g) || []).shift() || "") + (r.pop() || "")).toUpperCase()) + "</span>") + '</div></div><div class="d-flex flex-column"><a href="' + i + '?userId=' + a.userId + '" class="text-heading text-truncate"><span class="fw-medium">' + s + "</span></a><small>" + o + "</small></div></div>"
            }
        }, {
            targets: 3,
            render: function (e, t, a, n) {
                a = new Date(a.startDate);
                return '<span class="text-heading">' + a.getDate() + "</span>"
            }
        }, {
            targets: 4,
            render: function (e, t, a, n) {
                a = new Date(a.endDate);
                return '<span class="text-heading">' + a.getDate() + "</span>"
            }
        }, {
            targets: 6,
            render: function (e, t, a, n) {
                a = a.bookingStatus.bookingStatusId;
                return '<span class="badge ' + r[a].class + '" text-capitalized>' + r[a].title + "</span>"
            }
        }, {
            targets: -1,
            title: "Actions",
            searchable: !1,
            orderable: !1,
            render: function (e, t, a, n) {
                return '<div class="d-flex align-items-center"><a class="btn btn-icon"><i class="bx bx-trash bx-md" style="color: #ff5555"></i></a><a href="' + i + '?userId=' + a.userId + '" class="btn btn-icon"><i class="bx bx-show bx-md"></i></a><a href="javascript:;" class="btn btn-icon dropdown-toggle hide-arrow" data-bs-toggle="dropdown"><i class="bx bx-dots-vertical-rounded bx-md"></i></a><div class="dropdown-menu dropdown-menu-end m-0"><a href="javascript:;" class="dropdown-item">Edit</a><a class="dropdown-item" style="color: #ff4d4d">Suspend</a></div></div>'
            }
        }],
        order: [[2, "desc"]],
        dom: '<"row"<"col-md-2"<"ms-n2"l>><"col-md-10"<"dt-action-buttons text-xl-end text-lg-start text-md-end text-start d-flex align-items-center justify-content-end flex-md-row flex-column mb-6 mb-md-0 mt-n6 mt-md-0 gap-md-4"fB>>>t<"row"<"col-sm-12 col-md-6"i><"col-sm-12 col-md-6"p>>',
        language: {
            sLengthMenu: "_MENU_",
            search: "",
            searchPlaceholder: "Search User",
            paginate: {
                next: '<i class="bx bx-chevron-right bx-18px"></i>',
                previous: '<i class="bx bx-chevron-left bx-18px"></i>'
            }
        },
        buttons: [{
            extend: "collection",
            className: "btn btn-label-secondary dropdown-toggle me-4",
            text: '<i class="bx bx-export me-2 bx-sm"></i>Export',
            buttons: [{
                extend: "print",
                text: '<i class="bx bx-printer me-2" ></i>Print',
                className: "dropdown-item",
                exportOptions: {
                    columns: [1, 2, 3, 4, 5], format: {
                        body: function (e, t, a) {
                            var n;
                            return e.length <= 0 ? e : (e = $.parseHTML(e), n = "", $.each(e, function (e, t) {
                                void 0 !== t.classList && t.classList.contains("user-name") ? n += t.lastChild.firstChild.textContent : void 0 === t.innerText ? n += t.textContent : n += t.innerText
                            }), n)
                        }
                    }
                },
                customize: function (e) {
                    $(e.document.body).css("color", n).css("border-color", t).css("background-color", a), $(e.document.body).find("table").addClass("compact").css("color", "inherit").css("border-color", "inherit").css("background-color", "inherit")
                }
            }, {
                extend: "csv",
                text: '<i class="bx bx-file me-2" ></i>Csv',
                className: "dropdown-item",
                exportOptions: {
                    columns: [1, 2, 3, 4, 5], format: {
                        body: function (e, t, a) {
                            var n;
                            return e.length <= 0 ? e : (e = $.parseHTML(e), n = "", $.each(e, function (e, t) {
                                void 0 !== t.classList && t.classList.contains("user-name") ? n += t.lastChild.firstChild.textContent : void 0 === t.innerText ? n += t.textContent : n += t.innerText
                            }), n)
                        }
                    }
                }
            }, {
                extend: "excel",
                text: '<i class="bx bxs-file-export me-2"></i>Excel',
                className: "dropdown-item",
                exportOptions: {
                    columns: [1, 2, 3, 4, 5], format: {
                        body: function (e, t, a) {
                            var n;
                            return e.length <= 0 ? e : (e = $.parseHTML(e), n = "", $.each(e, function (e, t) {
                                void 0 !== t.classList && t.classList.contains("user-name") ? n += t.lastChild.firstChild.textContent : void 0 === t.innerText ? n += t.textContent : n += t.innerText
                            }), n)
                        }
                    }
                }
            }, {
                extend: "pdf",
                text: '<i class="bx bxs-file-pdf me-2"></i>Pdf',
                className: "dropdown-item",
                exportOptions: {
                    columns: [1, 2, 3, 4, 5], format: {
                        body: function (e, t, a) {
                            var n;
                            return e.length <= 0 ? e : (e = $.parseHTML(e), n = "", $.each(e, function (e, t) {
                                void 0 !== t.classList && t.classList.contains("user-name") ? n += t.lastChild.firstChild.textContent : void 0 === t.innerText ? n += t.textContent : n += t.innerText
                            }), n)
                        }
                    }
                }
            }, {
                extend: "copy",
                text: '<i class="bx bx-copy me-2" ></i>Copy',
                className: "dropdown-item",
                exportOptions: {
                    columns: [1, 2, 3, 4, 5], format: {
                        body: function (e, t, a) {
                            var n;
                            return e.length <= 0 ? e : (e = $.parseHTML(e), n = "", $.each(e, function (e, t) {
                                void 0 !== t.classList && t.classList.contains("user-name") ? n += t.lastChild.firstChild.textContent : void 0 === t.innerText ? n += t.textContent : n += t.innerText
                            }), n)
                        }
                    }
                }
            }]
        }, {
            text: '<i class="bx bx-plus bx-sm me-0 me-sm-2"></i><span class="d-none d-sm-inline-block">Add New User</span>',
            className: "add-new btn btn-primary",
            attr: {"data-bs-toggle": "offcanvas", "data-bs-target": "#offcanvasAddUser"}
        }],
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
            this.api().columns(2).every(function () {
                var t = this,
                    a = $('<select id="UserRole" class="form-select text-capitalize" style="width: 100%;\n' +
                        '    border-radius: 10px;\n' +
                        '    border: 3px solid #efefef;\n' +
                        '    padding: 10px 10px;\n' +
                        '}"><option value=""> Select Brand </option></select>').appendTo(".user_role").on("change", function () {
                        var e = $.fn.dataTable.util.escapeRegex($(this).val());
                        t.search(e ? "^" + e + "$" : "", !0, !1).draw()
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
                this.api().columns(6).every(function () {
                    var t = this,
                        a = $('<select id="FilterTransaction" class="form-select text-capitalize" style="width: 100%;\n' +
                            '    border-radius: 10px;\n' +
                            '    border: 3px solid #efefef;\n' +
                            '    padding: 10px 10px;\n' +
                            '}"><option value=""> Select Status </option></select>').appendTo(".user_status").on("change", function () {
                            var e = $.fn.dataTable.util.escapeRegex($(this).val());
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
    }),

        setTimeout(() => {
            $(".dataTables_filter .form-control").removeClass("form-control-sm"), $(".dataTables_length .form-select").removeClass("form-select-sm")
        }, 300)


// // window.Helpers.initCustomOptionCheck();
// // var e = [].slice.call(document.querySelectorAll(".flatpickr-validation"))
// //     , e = (e && e.forEach(e => {
// //         e.flatpickr({
// //             enableTime: !1,
// //             dateFormat: "Y-m-d",
// //             maxDate: "today",
// //             onChange: function () {
// //                 i.revalidateField("dob")
// //             },
// //             static: !0
// //         })
// //     }
// // ));
// //
// // function deleteUser(userId){
// //     Swal.fire({
// //         title: 'Are you sure?',
// //         text: "You won't be able to revert this!",
// //         icon: 'warning',
// //         showCancelButton: !0,
// //         confirmButtonText: 'Yes, delete it!',
// //         cancelButtonText: 'No, cancel!',
// //         reverseButtons: true,
// //         customClass: {
// //             confirmButton: "btn btn-primary me-3",
// //             cancelButton: "btn btn-label-secondary"
// //         },
// //         buttonsStyling: !1
// //
// //     }).then((result) => {
// //         if (result.isConfirmed) {
// //             fetch(`/admin/user-management/delete-user?userId=${userId}`, {
// //                 method: 'GET',
// //             }).then(response => {
// //                 if (response.ok) {
// //                     response.json().then(data => {
// //                         console.log(data);
// //                         Swal.fire({
// //                             icon: 'success',
// //                             title: 'Success',
// //                             text: data.message,
// //                             showConfirmButton: false,
// //                             timer: 1500
// //                         });
// //                         $('.datatables-users').DataTable().ajax.reload(null, !1)
// //                     });
// //                 } else {
// //                     response.json().then(data => {
// //                         console.log(data);
// //                         Swal.fire({
// //                             icon: 'warning',
// //                             title: 'Warning',
// //                             text: "Deletion failed: "+data.error,
// //                             showConfirmButton: false,
// //                             timer: 2500
// //                         });
// //                         $('.datatables-users').DataTable().ajax.reload(null, !1)
// //                     });
// //                 }
// //             });
// //         }
// //     })
// // }
// //
// // function suspendUser(userId){
// //     Swal.fire({
// //         title: 'Are you sure?',
// //         text: "You won't be able to revert this!",
// //         icon: 'warning',
// //         showCancelButton: !0,
// //         confirmButtonText: 'Yes, suspend it!',
// //         cancelButtonText: 'No, cancel!',
// //         reverseButtons: true,
// //         customClass: {
// //             confirmButton: "btn btn-primary me-3",
// //             cancelButton: "btn btn-label-secondary"
// //         },
// //         buttonsStyling: !1
// //
// //     }).then((result) => {
// //         if (result.isConfirmed) {
// //             fetch(`/admin/user-management/suspend-user?userId=${userId}`, {
// //                 method: 'GET',
// //             }).then(response => {
// //                 if (response.ok) {
// //                     response.json().then(data => {
// //                         console.log(data);
// //                         Swal.fire({
// //                             icon: 'success',
// //                             title: 'Success',
// //                             text: data.message,
// //                             showConfirmButton: false,
// //                             timer: 1500
// //                         });
// //                         $('.datatables-users').DataTable().ajax.reload(null, !1)
// //                     });
// //                 } else {
// //                     response.json().then(data => {
// //                         console.log(data);
// //                         Swal.fire({
// //                             icon: 'warning',
// //                             title: 'Warning',
// //                             text: "Suspension failed: "+data.error,
// //                             showConfirmButton: false,
// //                             timer: 2500
// //                         });
// //                         $('.datatables-users').DataTable().ajax.reload(null, !1)
// //                     });
// //                 }
// //             });
// //         }
// //     })
// // }
//
//
