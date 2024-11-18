$(function () {

    let t, a, n;
    n = (isDarkStyle ? (t = config.colors_dark.borderColor, a = config.colors_dark.bodyBg, config.colors_dark) : (t = config.colors.borderColor, a = config.colors.bodyBg, config.colors)).headingColor;
    var e, s = $(".datatables-users"), o = $(".select2"), i = "/admin/user-management/user-detail", r = {
        'PENDING': {title: "Pending", class: "bg-label-warning"},
        'ACTIVATED': {title: "Active", class: "bg-label-success"},
        'LOCKED': {title: "Locked", class: "bg-label-secondary"},
        'DELETED': {title: "Deleted", class: "bg-label-error"}
    };
    o.length && (o = o).wrap('<div class="position-relative"></div>').select2({
        placeholder: "Select Country",
        dropdownParent: o.parent()
    }),
    s.length && (e = s.DataTable({
        ajax: {
            url: "/api/users/",
            method: "GET",
            dataSrc: ''
        },
        columns: [
            {data: "userId"},
            {data: "userId"},
            {data: "username"},
            {data: "role"},
            {data: "dob"},
            {data: "phone"},
            {data: "status"},
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
            targets: 1,
            orderable: !1,
            checkboxes: {selectAllRender: '<input type="checkbox" class="form-check-input">'},
            render: function () {
                return '<input type="checkbox" class="dt-checkboxes form-check-input" >'
            },
            searchable: !1
        }, {
            targets: 2,
            responsivePriority: 4,
            render: function (e, t, a, n) {
                var s = a.username, o = a.email, r = a.avatar == null ? null : a.avatar;
                return '<div class="d-flex justify-content-start align-items-center user-name"><div class="avatar-wrapper"><div class="avatar avatar-sm me-4">' + (r ? '<img src="' + assetsPath + "img/avatars/" + r + '" alt="Avatar" class="rounded-circle">' : '<span class="avatar-initial rounded-circle bg-label-' + ["success", "danger", "warning", "info", "dark", "primary", "secondary"][Math.floor(6 * Math.random())] + '">' + (r = (((r = (s = a.username).match(/\b\w/g) || []).shift() || "") + (r.pop() || "")).toUpperCase()) + "</span>") + '</div></div><div class="d-flex flex-column"><a href="' + i + '?userId=' + a.userId + '" class="text-heading text-truncate"><span class="fw-medium">' + s + "</span></a><small>" + o + "</small></div></div>"
            }
        }, {
            targets: 3,
            render: function (e, t, a, n) {
                a = a.role;
                return "<span class='text-truncate d-flex align-items-center text-heading'>" + {
                    'Driver': '<i class="bx bx-card text-primary me-2"></i>',
                    'Car Owner': '<i class="bx bx-car text-warning me-2"></i>',
                    'Customer': '<i class="bx bx-user text-success me-2"></i>',
                    'THE FUCK, WHERE IS MY ROLE ?': '<i class="bx bx-error text-danger me-2"></i>',
                    Admin: '<i class="bx bx-desktop text-danger me-2"></i>'
                }[a] + a + "</span>"
            }
        }, {
            targets: 4,
            render: function (e, t, a, n) {
                return '<span class="text-heading">' + a.dob + "</span>"
            }
        }, {
            targets: 6,
            render: function (e, t, a, n) {
                a = a.status;
                return '<span class="badge ' + r[a].class + '" text-capitalized>' + r[a].title + "</span>"
            }
        }, {
            targets: -1,
            title: "Actions",
            searchable: !1,
            orderable: !1,
            render: function (e, t, a, n) {
                return '<div class="d-flex align-items-center"><a href="javascript:;" class="btn btn-icon delete-record"><i class="bx bx-trash bx-md"></i></a><a href="' + i + '?userId=' + a.userId + '" class="btn btn-icon"><i class="bx bx-show bx-md"></i></a><a href="javascript:;" class="btn btn-icon dropdown-toggle hide-arrow" data-bs-toggle="dropdown"><i class="bx bx-dots-vertical-rounded bx-md"></i></a><div class="dropdown-menu dropdown-menu-end m-0"><a href="javascript:;" class="dropdown-item">Edit</a><a href="javascript:;" class="dropdown-item">Suspend</a></div></div>'
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
            this.api().columns(3).every(function () {
                var t = this,
                    a = $('<select id="UserRole" class="form-select text-capitalize"><option value=""> Select Role </option></select>').appendTo(".user_role").on("change", function () {
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
                        a = $('<select id="FilterTransaction" class="form-select text-capitalize"><option value=""> Select Status </option></select>').appendTo(".user_status").on("change", function () {
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
        e.row($(this).parents("tr")).remove().draw()}),

        setTimeout(() => {
        $(".dataTables_filter .form-control").removeClass("form-control-sm"), $(".dataTables_length .form-select").removeClass("form-select-sm")
    }, 300)}),

    function () {
        // Track email validation state
        let isEmailBlurred = false;
        let emailCheckTimeout = null;


    var e = document.querySelectorAll(".phone-mask"), t = document.getElementById("addNewUserForm");
    e && e.forEach(function (e) {
        new Cleave(e, {phone: !0, phoneRegionCode: "VN"})
    }),
        i = FormValidation.formValidation(t, {
        fields: {
            username: {
                validators:
                    {
                        notEmpty: {
                                message: "Please enter username "
                        },
                        stringLength: {
                            min: 6,
                            max: 30,
                            message: "The name must be more than 6 and less than 30 characters long"
                        },
                        regexp: {
                            regexp: /^[a-zA-Z0-9 ]+$/,
                            message: "The name can only consist of alphabetical, number and space"
                        }
                    }
            },
            email: {
                validators: {
                    notEmpty: {
                        message: "Please enter your email"
                    },
                    emailAddress: {
                        message: "The value is not a valid email address"
                    },
                    // remote: {
                    //     url: "/api/users/check-email",
                    //     message: "The email is already taken",
                    //     method: "GET",
                    //     data: function () {
                    //         return {
                    //             email: t.querySelector('[name="email"]').value
                    //         }
                    //     },
                    // }
                    callback: {
                        callback: function (input) {
                            if (isEmailBlurred) {
                                return validateEmail(input.value);
                            }
                            return true;
                        }
                    }
                }
            },
            password: {
                validators: {
                    notEmpty: {
                        message: "Please enter your password"
                    },
                    stringLength: {
                        min: 6,
                        message: "Password must have at least 6 characters"
                    },
                    regexp: {
                        regexp: /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\W).{6,}$/,
                        message: "The password must contain at least one number, one lowercase and one uppercase letter, one special character"
                    },
                }
            },
            formConfirmPassword: {
                validators: {
                        notEmpty: {
                            message: "Please confirm password"
                        },
                        identical: {
                            compare: function () {
                                return t.querySelector('[name="formPassword"]').value
                            },
                            message: "The password and its confirm are not the same"
                        }
                }
            },
            dob: {
                validators: {
                    notEmpty: {
                        message: "Please select your DOB"
                    },
                    date: {
                        format: "YYYY-MM-DD",
                        message: "The value is not a valid date"
                    },
                    callback: {
                        message: "You must be at least 18 years old",
                        callback: function (input) {
                            const dob = input.value;
                            const [day, month, year] = dob.split('-');
                            const dobDate = new Date(year, month - 1, day);
                            const today = new Date();
                            const age = today.getFullYear() - dobDate.getFullYear();
                            const monthDiff = today.getMonth() - dobDate.getMonth();
                            const dayDiff = today.getDate() - dobDate.getDate();
                            return age > 18 || (age === 18 && (monthDiff > 0 || (monthDiff === 0 && dayDiff >= 0)));
                        }
                    }

                }
            },
            formRole: {
                validators: {
                    notEmpty: {
                        message: "Please select role"
                    }
                }
            },
            status: {
                validators: {
                    notEmpty: {
                        message: "Please select status"
                    }
                }
            },
        },
        plugins: {
            trigger: new FormValidation.plugins.Trigger,
            bootstrap5: new FormValidation.plugins.Bootstrap5({
                eleValidClass: "", rowSelector: function (e, t) {
                    return ".mb-6"
                }
            }),
            submitButton: new FormValidation.plugins.SubmitButton,
            defaultSubmit: new FormValidation.plugins.DefaultSubmit,
            autoFocus: new FormValidation.plugins.AutoFocus
        },

    })

    t.addEventListener("submit", function(e) {
        t.checkValidity() ? alert("Submitted!!!") : (e.preventDefault(),
            e.stopPropagation()),
            t.classList.add("was-validated")
    }, !1);


        // Handle email field blur/focus events
        const emailField = t.querySelector('[name="email"]');
        emailField.addEventListener('blur', () => {
            isEmailBlurred = true;
            i.revalidateField('email');
        });

        emailField.addEventListener('focus', () => {
            isEmailBlurred = false;
            i.revalidateField('email');
        });


        const validateEmail = async function (email) {
            const check = await checkEmailAvailability(email);
            if(check){
                return {
                    valid: true,
                    message: 'Email is available',
                }
            }
            else{
                return {
                    valid: false,
                    message: 'Email is already taken',
                }
            }
        }

        async function checkEmailAvailability(email) {
            const response = await fetch(`/api/users/check-email?email=${email}`);
            return await response.json();

        }
}();

    window.Helpers.initCustomOptionCheck();
    var e = [].slice.call(document.querySelectorAll(".flatpickr-validation"))
        , e = (e && e.forEach(e => {
            e.flatpickr({
                enableTime: !1,
                dateFormat: "Y-m-d",
                onChange: function() {
                    i.revalidateField("dob")
                }
            })
        }
    ));



