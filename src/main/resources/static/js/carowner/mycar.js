

// Confirm booking modal
function showConfirmBookingModal(carId) {
    $('#confirmBookingModal').modal({

    });
    getBookingList(carId);
}


//Confirm booking
function confirmBooking() {
    Swal.fire({
        title: 'Confirm booking and receive deposit from customer!',
        icon: 'question',
        confirmButtonText: 'Yes',
        cancelButtonText: 'No',
        showCancelButton: true,
        customClass: {
            confirmButton: 'custom-confirm-btn',
            cancelButton: 'custom-cancel-btn'
        }
    }).then((result) => {
        if (result.isConfirmed) {
            console.log('User confirmed the booking');
            // Call API to confirm booking
            fetch('/api/car-owner/confirm-booking', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify({
                    carId: 1
                })
            }).then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error('Failed to confirm booking');
            }).then(data => {
                console.log('Confirm booking successfully', data);
                Swal.fire({
                    title: 'Booking Successfully Confirmed.',
                    text: data.message,
                    icon: 'success',
                    confirmButtonText: 'OK'
                });
            }).catch(error => {
                console.error('Error confirming booking', error);
                Swal.fire({
                    title: 'Error!',
                    text: 'An error occurred. Please try again.',
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
            });
        } else {
            console.log('User canceled the action');
        }
    });
}

//Api get booking list of car
let bookingList = [];
function getBookingList(carId) {
    const confirmBookingModalBody = document.getElementById('confirmBookingModalBody');
    fetch('/car-owner/api/booking-list?carId=' + carId)
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Failed to get booking list');
        })
        .then(data => {
            console.log('Booking list:', data);
            if(data.length === 0) {
                Swal.fire({
                    title: 'No booking found!',
                    text: 'There is no booking for this car.',
                    icon: 'info',
                    confirmButtonText: 'OK'
                });
            }else{
                bookingList = data;
                const bookingElement = document.createElement('div');
                data.forEach(booking => {
                    const formatMoney = new Intl.NumberFormat('vi-VN', {
                        style: 'currency',
                        currency: 'VND',
                    }).format(booking.deposit);
                    const formatDateTime = new Intl.DateTimeFormat('en-US', {
                        dateStyle: 'medium',
                        timeStyle: 'short'
                    }).format(new Date(booking.lastModified));
                    const formatStartDate = new Intl.DateTimeFormat('en-US', {
                        dateStyle: 'medium',
                        timeStyle: 'short'
                    }).format(new Date(booking.startDate));
                    const formatEndDate = new Intl.DateTimeFormat('en-US', {
                        dateStyle: 'medium',
                        timeStyle: 'short'
                    }).format(new Date(booking.endDate));
                    let totalPrice = 0;
                    const rentalTime = new Date(booking.endDate).getTime() - new Date(booking.startDate).getTime();
                    const rentalDays = rentalTime / (1000 * 3600 * 24);
                    const rentalHours = rentalTime / (1000 * 3600);
                    console.log(rentalTime);
                    console.log(rentalDays);
                    console.log(rentalHours);
                    console.log(booking.totalPrice);
                    if(rentalDays > 0 && rentalDays < 1){
                        totalPrice = booking.deposit + booking.totalPrice * rentalHours;
                    }
                    if(rentalDays >= 1){
                        totalPrice = booking.deposit + booking.totalPrice * parseInt(rentalDays);
                    }
                    const formatTotalPrice = new Intl.NumberFormat('vi-VN', {
                        style: 'currency',
                        currency: 'VND',
                    }).format(totalPrice);

                    bookingElement.innerHTML += `
                                <div class="card border-0 shadow-sm hover-shadow mb-3">
    <div class="card-body p-4">
        <div class="row g-4">
            <!-- Booking Info Section -->
            <div class="col-12 col-lg-12">
                <div class="d-flex align-items-center mb-3">
                    <div class="booking-icon-wrapper me-3">
                        <i class="fas fa-bookmark text-primary fs-4"></i>
                    </div>
                    <h5 class="card-title-booking mb-0 fw-bold">
                        Booking ID: <span class="text-primary">${booking.bookingId}</span>
                    </h5>
                </div>
                
                <div class="booking-details">
                    <!-- Customer Info -->
                    <div class="booking-info-item mb-2">
                        <i class="fas fa-user text-secondary mr-2"></i>
                        <span>${booking.user.username}</span>
                    </div>

                    <!-- Booking Date -->
                    <div class="booking-info-item mb-2">
                        <i class="fas fa-calendar-alt text-secondary mr-2"></i>
                        <span class="fw-semibold mr-2">Booking Date:</span>
                        <span>${formatDateTime}</span>
                    </div>

                    <!-- Rental Period -->
                    <div class="rental-period bg-light rounded p-3 mb-2">
                        <div class="d-flex align-items-center mb-2">
                            <div class="rental-date mr-4">
                                <i class="fas fa-calendar-plus text-success mr-2"></i>
                                <span class="fw-semibold">Start:</span>
                                <span>${formatStartDate}</span>
                            </div>
                            <div class="rental-date">
                                <i class="fas fa-calendar-minus text-danger mr-2"></i>
                                <span class="fw-semibold">End:</span>
                                <span>${formatEndDate}</span>
                            </div>
                        </div>
                    </div>

                    <!-- Deposit -->
                    <div class="booking-info-item">
                        <i class="fas fa-wallet text-warning mr-2"></i>
                        <span class="fw-semibold mr-2">Deposit:</span>
                        <span class="deposit-amount fw-bold">${formatMoney}</span>
                    </div>
                    
                    <!-- Total -->
                    <div class="booking-info-item">
                        <i class="fas fa-money-bill-wave text-success mr-2"></i>
                        <span class="fw-semibold mr-2">Total:</span>
                        <span class="deposit-amount fw-bold">${formatTotalPrice}</span>
                    </div>
                </div>
            </div>

            <!-- Action Buttons Section -->
            <div class="col-12 col-lg-12 d-flex justify-content-center justify-content-lg-end" style="height: fit-content;">
                <div class="d-flex" style="gap: 10px;">
                    <button class="btns btn-confirm px-4" onclick="confirmBooking()">
                        <i class="fas fa-check mr-2"></i>Confirm
                    </button>
                    <button class="btns btn-cancel px-4">
                        <i class="fas fa-times mr-2"></i>Cancel
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
                            `;
                });
                confirmBookingModalBody.innerHTML = bookingElement.innerHTML;
            }
        })
        .catch(error => {
            console.error('Error getting booking list', error);
            Swal.fire({
                title: 'Error!',
                text: 'An error occurred. Please try again.',
                icon: 'error',
                confirmButtonText: 'OK'
            });
        });
}