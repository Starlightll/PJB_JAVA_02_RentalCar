$(function () {
    let t, a, n;
    n = (isDarkStyle ? (t = config.colors_dark.borderColor, a = config.colors_dark.bodyBg, config.colors_dark) : (t = config.colors.borderColor, a = config.colors.bodyBg, config.colors)).headingColor;
    var e, s = $(".datatables-users"), o = $(".select2"), i = "/admin/user-management/user-detail", r = {
        'PENDING': {title: "Pending", class: "bg-label-warning"},
        'ACTIVATED': {title: "Active", class: "bg-label-success"},
        'LOCKED': {title: "Locked", class: "bg-label-danger"},
        'DELETED': {title: "Deleted", class: "bg-label-secondary"}
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
                return '<div class="d-flex justify-content-start align-items-center user-name"><div class="avatar-wrapper"><div class="avatar avatar-sm me-4">' + (r ? '<img src="' + "\\" + r + '" alt="Avatar" class="rounded-circle">' : '<span class="avatar-initial rounded-circle bg-label-' + ["success", "danger", "warning", "info", "dark", "primary", "secondary"][Math.floor(6 * Math.random())] + '">' + (r = (((r = (s = a.username).match(/\b\w/g) || []).shift() || "") + (r.pop() || "")).toUpperCase()) + "</span>") + '</div></div><div class="d-flex flex-column"><a href="' + i + '?userId=' + a.userId + '" class="text-heading text-truncate"><span class="fw-medium">' + s + "</span></a><small>" + o + "</small></div></div>"
            }
        }, {
            targets: 3,
            render: function (e, t, a, n) {
                a = a.role;
                return "<span class='text-truncate d-flex align-items-center text-heading'>" + {
                    'Driver': '<i class="bx bx-user-pin text-info me-2"></i>',
                    'Car Owner': '<i class="bx bx-car text-warning me-2"></i>',
                    'Customer': '<i class="bx bx-user text-success me-2"></i>',
                    'Admin': '<i class="bx bx-desktop text-danger me-2"></i>'
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
                return '<div class="d-flex align-items-center"><a onclick="deleteUser('+a.userId+')" class="btn btn-icon"><i class="bx bx-trash bx-md" style="color: #ff5555"></i></a><a href="' + i + '?userId=' + a.userId + '" class="btn btn-icon"><i class="bx bx-show bx-md"></i></a><a href="javascript:;" class="btn btn-icon dropdown-toggle hide-arrow" data-bs-toggle="dropdown"><i class="bx bx-dots-vertical-rounded bx-md"></i></a><div class="dropdown-menu dropdown-menu-end m-0"><a href="javascript:;" class="dropdown-item">Edit</a><a onclick="suspendUser('+a.userId+')" class="dropdown-item" style="color: #ff4d4d">Suspend</a></div></div>'
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
        e.row($(this).parents("tr")).remove().draw()
    }),

        setTimeout(() => {
            $(".dataTables_filter .form-control").removeClass("form-control-sm"), $(".dataTables_length .form-select").removeClass("form-select-sm")
        }, 300)
}),

    function () {
        // Track email validation state
        let isEmailBlurred = false;
        // Track phone validation state
        let isPhoneBlurred = false;

        var e = document.querySelectorAll(".phone-mask"),
            t = document.getElementById("addNewUserForm");
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
                                    max: 30,
                                    message: "The name must be more than 6 and less than 30 characters long"
                                },
                                // regexp: {
                                //     regexp: /^[a-zA-Z0-9 ]+$/,
                                //     message: "The name can only consist of alphabetical, number and space"
                                // }
                            }
                    },
                    email: {
                        validators: {
                            // notEmpty: {
                            //     message: "Please enter your email"
                            // },
                            // emailAddress: {
                            //     message: "The value is not a valid email address"
                            // },
                            callback: {
                                callback: function (input) {
                                    //Regex for email
                                    //The email must start with a letter
                                    //The email must have exactly one @ symbol
                                    //The email must have at least one dot
                                    //The email must have at least 2 characters after the last dot
                                    const emailRegex = /^[a-zA-Z][a-zA-Z0-9._%+-]*@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
                                    if (input.value.trim() === '') {
                                        return {
                                            valid: false,
                                            message: "The email is required"
                                        };
                                    } else {
                                        if (isEmailBlurred) {
                                            if (!emailRegex.test(input.value)) {
                                                return {
                                                    valid: false,
                                                    message: "The email is not valid"
                                                };
                                            }
                                            return validateEmail(input.value);
                                        }
                                        return true;
                                    }
                                }
                            }
                        }
                    },
                    password: {
                        validators: {
                            callback: {
                                callback: function (input) {
                                    const password = input.value;
                                    if (password.trim() === '') {
                                        return {
                                            valid: false,
                                            message: "The password is required"
                                        };
                                    } else {
                                        const hasNumber = /\d/.test(password);
                                        const hasUpperCase = /[A-Z]/.test(password);
                                        const hasLowerCase = /[a-z]/.test(password);
                                        const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
                                        let errors = [];

                                        if (password.length < 8) {
                                            errors.push("Password must be at least 8 characters long");
                                        }
                                        if (password.length > 30) {
                                            errors.push("Password must be less than 30 characters long");
                                        }
                                        if (!hasNumber) {
                                            errors.push("Password must contain at least one number");
                                        }
                                        if (!hasUpperCase) {
                                            errors.push("Password must contain at least one uppercase letter");
                                        }
                                        if (!hasLowerCase) {
                                            errors.push("Password must contain at least one lowercase letter");
                                        }
                                        if (!hasSpecialChar) {
                                            errors.push("Password must contain at least one special character");
                                        }

                                        if (errors.length > 0) {
                                            return {
                                                valid: false,
                                                message: errors.join('<br>')
                                            }
                                        }
                                        return true;
                                    }
                                },
                            }
                        }
                    },
                    formConfirmPassword: {
                        validators: {
                            notEmpty: {
                                message: "Please confirm password"
                            },
                            identical: {
                                compare: function () {
                                    return t.querySelector('[name="password"]').value;
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

                                callback: function (input) {
                                    const dob = input.value;
                                    const [year, month, day] = dob.split('-');
                                    const dobDate = new Date(year, month - 1, day);
                                    const today = new Date();
                                    const age = today.getFullYear() - dobDate.getFullYear();
                                    const monthDiff = today.getMonth() - dobDate.getMonth();
                                    const dayDiff = today.getDate() - dobDate.getDate();
                                    //Age must be greater than 18
                                    if (age < 18 || (age === 18 && (monthDiff < 0 || (monthDiff === 0 && dayDiff <= 0)))) {
                                        return {
                                            valid: false,
                                            message: "You must be at least 18 years old"
                                        }
                                    }
                                    //Age must be less than 100
                                    if (age > 100) {
                                        return {
                                            valid: false,
                                            message: "You must be less than 100 years old"
                                        }
                                    }
                                    return true;
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
                    phone: {
                        validators: {
                            // notEmpty: {
                            //     message: "Please enter your phone"
                            // },
                            callback: {
                                callback: function (input) {
                                    //Regex for phone vietnamese phone number
                                    //Remove all spaces, dashes, dots, and parentheses
                                    //The phone number must start with 03, 05, 07, 08, or 09
                                    //The phone number must have exactly 10 digits
                                    if (input.value.trim() === '') {
                                        return {
                                            valid: false,
                                            message: "The phone number is required"
                                        };
                                    } else {
                                        if (isPhoneBlurred) {
                                            const phoneRegex = /^(03|05|07|08|09)[0-9]{8}$/;
                                            const formattedPhone = input.value.trim().replace(/[\s\\.\-\\(\\)]/g, '');
                                            const isValidFormat = phoneRegex.test(formattedPhone);
                                            if (!isValidFormat) {
                                                return {
                                                    valid: false,
                                                    message: "The phone number is not valid"
                                                };
                                            }
                                            return validatePhone(input.value);
                                        }
                                        return true;
                                    }
                                }
                            },
                        }
                    }
                },
                plugins: {
                    trigger: new FormValidation.plugins.Trigger,
                    bootstrap5: new FormValidation.plugins.Bootstrap5({
                        eleValidClass: "", rowSelector: function (e, t) {
                            return ".mb-6"
                        }
                    }),
                    // submitButton: new FormValidation.plugins.SubmitButton,
                    // defaultSubmit: new FormValidation.plugins.DefaultSubmit,
                    autoFocus: new FormValidation.plugins.AutoFocus,
                },
            })

        t.addEventListener("submit", function (e) {
            e.preventDefault();
            i.validate().then(function (e) {
                if ("Valid" === e) {
                    console.log("Submitted!!!");
                    const data = new FormData(t);
                    fetch('/admin/user-management/add-user', {
                        method: 'POST',
                        //     headers: {
                        //         'Accept': 'application/json',
                        //         'Content-Type': 'application/json',
                        //         'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content')
                        // },
                        body: data
                    }).then(response => {
                        if (response.ok) {
                            response.json().then(data => {
                                console.log(data);
                                Swal.fire({
                                    icon: 'success',
                                    title: 'Success',
                                    text: data.message,
                                    showConfirmButton: false,
                                    timer: 1500
                                });
                                $('#offcanvasAddUser').offcanvas('hide');
                                $('#addNewUserForm').trigger('reload');
                                $('.datatables-users').DataTable().ajax.reload(null, !1)
                            });
                        } else {
                            response.json().then(data => {
                                console.log(data);
                                Swal.fire({
                                    icon: 'warning',
                                    title: 'Warning',
                                    text: "Add user failed",
                                    showConfirmButton: false,
                                    timer: 1500
                                });
                            });
                        }
                    });
                }
            })
        });


        // Handle email field blur/focus events
        const emailField = t.querySelector('[name="email"]');
        emailField.addEventListener('blur', () => {
            isEmailBlurred = true;
            i.revalidateField('email');
        });

        emailField.addEventListener('focus', () => {
            isEmailBlurred = false;
            // i.revalidateField('email');
        });


        const validateEmail = async function (email) {
            const check = await checkEmailAvailability(email);
            if (check) {
                return {
                    valid: true,
                    message: 'Email is available',
                }
            } else {
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


        // Handle phone field blur/focus events
        const phoneField = t.querySelector('[name="phone"]');
        phoneField.addEventListener('blur', () => {
            isPhoneBlurred = true;
            i.revalidateField('phone');
        });

        phoneField.addEventListener('focus', () => {
            isPhoneBlurred = false;
            // i.revalidateField('phone');
        });

        const validatePhone = async function (phone) {
            const check = await checkPhoneAvailability(phone);
            if (check) {
                console.log("Phone is available");
                return {
                    valid: true,
                    message: 'Phone is available',
                }
            } else {
                console.log("Phone is already taken");
                return {
                    valid: false,
                    message: 'Phone is already taken',
                }
            }
        }

        async function checkPhoneAvailability(phone) {
            const response = await fetch(`/api/users/check-phone?phone=${phone}`);
            return await response.json();

        }


    }();

window.Helpers.initCustomOptionCheck();
var e = [].slice.call(document.querySelectorAll(".flatpickr-validation"))
    , e = (e && e.forEach(e => {
        e.flatpickr({
            enableTime: !1,
            dateFormat: "Y-m-d",
            maxDate: "today",
            onChange: function () {
                i.revalidateField("dob")
            },
            static: !0
        })
    }
));

function deleteUser(userId){
    Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: !0,
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'No, cancel!',
        reverseButtons: true,
        customClass: {
            confirmButton: "btn btn-primary me-3",
            cancelButton: "btn btn-label-secondary"
        },
        buttonsStyling: !1

    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`/admin/user-management/delete-user?userId=${userId}`, {
                method: 'GET',
            }).then(response => {
                if (response.ok) {
                    response.json().then(data => {
                        console.log(data);
                        Swal.fire({
                            icon: 'success',
                            title: 'Success',
                            text: data.message,
                            showConfirmButton: false,
                            timer: 1500
                        });
                        $('.datatables-users').DataTable().ajax.reload(null, !1)
                    });
                } else {
                    response.json().then(data => {
                        console.log(data);
                        Swal.fire({
                            icon: 'warning',
                            title: 'Warning',
                            text: "Deletion failed: "+data.error,
                            showConfirmButton: false,
                            timer: 2500
                        });
                        $('.datatables-users').DataTable().ajax.reload(null, !1)
                    });
                }
            });
        }
    })
}

function suspendUser(userId){
    Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: !0,
        confirmButtonText: 'Yes, suspend it!',
        cancelButtonText: 'No, cancel!',
        reverseButtons: true,
        customClass: {
            confirmButton: "btn btn-primary me-3",
            cancelButton: "btn btn-label-secondary"
        },
        buttonsStyling: !1

    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`/admin/user-management/suspend-user?userId=${userId}`, {
                method: 'GET',
            }).then(response => {
                if (response.ok) {
                    response.json().then(data => {
                        console.log(data);
                        Swal.fire({
                            icon: 'success',
                            title: 'Success',
                            text: data.message,
                            showConfirmButton: false,
                            timer: 1500
                        });
                        $('.datatables-users').DataTable().ajax.reload(null, !1)
                    });
                } else {
                    response.json().then(data => {
                        console.log(data);
                        Swal.fire({
                            icon: 'warning',
                            title: 'Warning',
                            text: "Suspension failed: "+data.error,
                            showConfirmButton: false,
                            timer: 2500
                        });
                        $('.datatables-users').DataTable().ajax.reload(null, !1)
                    });
                }
            });
        }
    })
}


