
    let currentPage = 1;
    let currentStarRating = 0;
    let maxLimit = 1;
    let totalPages = 1; //phaan trang mac dinh

    function formatDate(dateString) {
    const date = new Date(dateString);

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Tháng bắt đầu từ 0
    const year = date.getFullYear();
    let hours = date.getHours();
    const minutes = String(date.getMinutes()).padStart(2, '0');

    let period = 'AM';  // Mặc định là AM
    if (hours >= 12) {
    period = 'PM';
    if (hours > 12) hours -= 12;
} else if (hours === 0) {
    hours = 12;
}

    hours = String(hours).padStart(2, '0');

    // Trả về định dạng mong muốn
    return `${day}/${month}/${year} - ${hours}:${minutes} ${period}`;
}

    // Function to load feedbacks based on star rating and page
    function loadFeedbacks(starRating = currentStarRating, page = currentPage) {
    const limit = document.getElementById("limit").value;

    fetch(`/homepage-customer/my-feedback/data?rating=${starRating}&page=${page}&limit=${limit}`)
    .then(response => response.json())
    .then(data => {
    const feedbackList = document.querySelector('.feedback-list');
    feedbackList.innerHTML = ''; // Clear the list before loading new data

    data.feedbacks.forEach(feedback => {
    const feedbackItem = document.createElement('div');
    feedbackItem.classList.add('feedback-item');
    feedbackItem.innerHTML = `
                    <div class="user-info">
                        <div>
                            <span style="font-size: 20px"> <i class="material-icons">person</i>${feedback.username}</span>
                        </div>
                       <div>
                            <div class="stars">${'★'.repeat(feedback.rating) + '☆'.repeat(5 - feedback.rating)}</div>
                            <span class="date">${formatDate(feedback.dateTime)}</span>
                        </div>
                    </div>
                    <p>${feedback.content}</p>
                    <div class="car-info">
                        <div class="image-container">
                            <img src="${feedback.frontImage}" alt="Car Image">
                        </div>
                        <div>
                            <strong>${feedback.carName}</strong>
                            <p style="margin-left: 10px">&#8226; From: ${formatDate(feedback.startDate)}</p>
                            <p style="margin-left: 10px">&#8226; To: ${formatDate(feedback.actualEndDate)}</p>
                        </div>
                    </div>
                `;
    feedbackList.appendChild(feedbackItem);
});

    // Update the current page display

    totalPages = data.totalPages;  // Set the total pages based on data
    renderPagination();
    maxLimit = data.totalFeedbacks;
})
    .catch(error => console.error('Error:', error));
}

    function renderPagination() {
    const paginationNumbers = document.getElementById('pagination-numbers');
    paginationNumbers.innerHTML = '';

    // Previous button
    const previousButton = document.createElement('button');
    previousButton.textContent = '«';
    previousButton.disabled = currentPage === 1;
    previousButton.onclick = () => {
    if (currentPage > 1) {
    currentPage -= 1;
    loadFeedbacks(currentStarRating, currentPage);
}
};
    paginationNumbers.appendChild(previousButton);

    // Page number buttons
    for (let i = 1; i <= totalPages; i++) {
    const pageButton = document.createElement('button');
    pageButton.textContent = i;
    pageButton.classList.toggle('active', i === currentPage);
    pageButton.onclick = () => {
    currentPage = i;
    loadFeedbacks(currentStarRating, currentPage);
};
    paginationNumbers.appendChild(pageButton);
}

    // Next button
    const nextButton = document.createElement('button');
    nextButton.textContent = '»';
    nextButton.disabled = currentPage === totalPages;
    nextButton.onclick = () => {
    if (currentPage < totalPages) {
    currentPage += 1;
    loadFeedbacks(currentStarRating, currentPage);
}
};
    paginationNumbers.appendChild(nextButton);
}


    // Adjust limit for items per page
    function adjustLimit(amount) {
    const limitInput = document.getElementById("limit");
    let currentLimit = parseInt(limitInput.value);
    currentLimit += amount;
    if (currentLimit < 1) currentLimit = 1;
    if (currentLimit > maxLimit) currentLimit = maxLimit;
    limitInput.value = currentLimit;

    // Reload feedbacks with updated limit
    loadFeedbacks(currentStarRating, currentPage);
}

    // Load initial feedbacks on page load
    document.addEventListener("DOMContentLoaded", function() {
    loadFeedbacks();
});

    // Go to the previous page
    function previousPage() {
    if (currentPage > 1) {
    currentPage -= 1;
    loadFeedbacks(currentStarRating, currentPage);
}
}

    // Go to the next page
    function nextPage() {
    currentPage += 1;
    loadFeedbacks(currentStarRating, currentPage);
}

    // Cập nhật giá trị `currentStarRating` khi chọn số sao và tải lại feedbacks
    function updateRatingAndLoadFeedbacks(starRating) {
    currentStarRating = starRating; // Cập nhật số sao hiện tại
    currentPage = 1; // Reset về trang 1
    loadFeedbacks(currentStarRating, currentPage);
}
