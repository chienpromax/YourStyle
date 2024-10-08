const apiURL = "https://provinces.open-api.vn/api/?depth=3";
let locationData = [];
const selectCity = document.getElementById("floatingcity");
const selectDistrict = document.getElementById("floatingdistrict");
const selectWard = document.getElementById("floatingward");

(async function loadAdress() {
  const reponse = await fetch(apiURL);
  locationData = await reponse.json();
  // console.log(locationData);
  // city
  selectCity.innerHTML = "<option selected>Chọn Tỉnh/thành phố</option>";
  // đổ dữ liệu vào input thành phố
  locationData.forEach((city) => {
    // city
    const optionCity = document.createElement("option");
    optionCity.value = city.code;
    optionCity.textContent = city.name;
    selectCity.appendChild(optionCity);
  });
})();

// Lắng nghe sự thay đổi khi chọn thành phố
selectCity.addEventListener("change", function () {
  selectDistrict.innerHTML =
    '<option value="" selected>Chọn Quận/huyện</option>';
  selectWard.innerHTML =
    '<option value="" selected>Chọn Xã/phường/thị trấn</option>';
  selectWard.disabled = true;

  const cityCode = this.value;
  if (cityCode) {
    const city = locationData.find((city) => city.code == cityCode);
    if (city) {
      // nếu như có thành phố
      populateDistricts(city.districts);
    }
    selectDistrict.disabled = false; // khi thành phố được chọn mở khóa input quận/huyện
  } else {
    selectDistrict.disabled = true;
    selectWard.disabled = true;
  }
});

// Tạo quận dựa trên thành phố được chọn
function populateDistricts(districts) {
  districts.forEach((district) => {
    const option = document.createElement("option");
    option.value = district.code;
    option.textContent = district.name;
    selectDistrict.appendChild(option);
  });
}
// Lắng nghe sự thay đổi khi chọn quận
selectDistrict.addEventListener("change", function () {
  selectWard.innerHTML = "<option selected>Chọn Xã,phường,thị trấn</option>";

  const districtCode = this.value;
  const cityCode = selectCity.value;
  if (districtCode && cityCode) {
    const city = locationData.find((city) => city.code == cityCode);
    const district = city.districts.find(
      (district) => district.code == districtCode
    );
    if (district) {
      populateWards(district.wards);
    }
    selectWard.disabled = false; // khi quận/huyện được chọn mở khóa input xã/phường/thị trấn
  } else {
    selectWard.disabled = true;
  }
});

// Tạo phường dựa trên quận được chọn
function populateWards(wards) {
  wards.forEach((ward) => {
    const option = document.createElement("option");
    option.value = ward.code;
    option.textContent = ward.name;
    selectWard.appendChild(option);
  });
}
