function updateCityName(selectElement) {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    const cityNameInput = document.getElementById("cityName");
    cityNameInput.value = selectedOption.text; // Lấy tên của tỉnh/thành phố
}

function updateDistrictName(selectElement) {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    const districtNameInput = document.getElementById("districtName");
    districtNameInput.value = selectedOption.text; // Lấy tên của Quận/Huyện
}

function updateWardName(selectElement) {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    const wardNameInput = document.getElementById("wardName");
    wardNameInput.value = selectedOption.text; // Lấy tên của
}