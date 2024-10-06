const urlAPI = "https://provinces.open-api.vn/api/?depth=3";
let locationData = [];

async function loadAdress(selectCityElement) {
  const reponse = await fetch(urlAPI);
  locationData = await reponse.json();
  // console.log(locationData);
  // city
  selectCityElement.innerHTML = "<option selected>Chọn Tỉnh/thành phố</option>";
  // đổ dữ liệu vào input thành phố
  locationData.forEach((city) => {
    const optionCity = document.createElement("option");
    optionCity.value = city.code;
    optionCity.textContent = city.name;
    selectCityElement.appendChild(optionCity);
  });
}
function attachEventHandlers() {
  const selectCities = document.querySelectorAll(".floatingcity");
  const selectDistricts = document.querySelectorAll(".floatingdistrict");
  const selectWards = document.querySelectorAll(".floatingward");

  selectCities.forEach((selectCity) => {
    selectCity.addEventListener("change", function () {
      const cityCode = this.value;
      const parentRow = this.closest(".row");
      const selectDistrict = parentRow.querySelector(".floatingdistrict");
      const selectWard = parentRow.querySelector(".floatingward");

      selectDistrict.innerHTML =
        '<option value="" selected>Chọn Quận/huyện</option>';
      selectWard.innerHTML =
        '<option value="" selected>Chọn Xã/phường/thị trấn</option>';
      selectWard.disabled = true;

      if (cityCode) {
        const city = locationData.find((city) => city.code == cityCode);
        if (city) {
          populateDistricts(city.districts, selectDistrict);
        }
        selectDistrict.disabled = false;
      } else {
        selectDistrict.disabled = true;
        selectWard.disabled = true;
      }
    });
  });

  selectDistricts.forEach((selectDistrict) => {
    selectDistrict.addEventListener("change", function () {
      const districtCode = this.value;
      const parentRow = this.closest(".row");
      const selectWard = parentRow.querySelector(".floatingward");
      const cityCode = parentRow.querySelector(".floatingcity").value;

      selectWard.innerHTML =
        "<option selected>Chọn Xã,phường,thị trấn</option>";

      if (districtCode && cityCode) {
        const city = locationData.find((city) => city.code == cityCode);
        const district = city.districts.find(
          (district) => district.code == districtCode
        );
        if (district) {
          populateWards(district.wards, selectWard);
        }
        selectWard.disabled = false;
      } else {
        selectWard.disabled = true;
      }
    });
  });
}

// Cập nhật populateDistricts và populateWards để chấp nhận trường select
function populateDistricts(districts, selectDistrict) {
  districts.forEach((district) => {
    const option = document.createElement("option");
    option.value = district.code;
    option.textContent = district.name;
    selectDistrict.appendChild(option);
  });
}

function populateWards(wards, selectWard) {
  wards.forEach((ward) => {
    const option = document.createElement("option");
    option.value = ward.code;
    option.textContent = ward.name;
    selectWard.appendChild(option);
  });
}

// xử lý  thêm địa chỉ
document.addEventListener("DOMContentLoaded", function () {
  const savedAddress = JSON.parse(localStorage.getItem("addresses")) || [];
  // console.log(savedAdress);
  // khi tải lại trang web thì không mất địa chỉ và nếu mảng rổng thì không hiện
  savedAddress.forEach((addressHtml) => {
    document
      .getElementById("displayAddress")
      .insertAdjacentHTML("beforeend", addressHtml);
  });
  // nhấn thêm địa chỉ
  document
    .getElementById("addAddress")
    .addEventListener("click", function (event) {
      event.preventDefault(); // ngăn chặn trang reload khi click vào thêm địa chỉ

      const newAddressHtml = `<div class="row">
                    <div class="col-md-12">
                      <h6>Địa chỉ ${savedAddress.length + 2}</h6>
                    </div>
                    <!-- Street -->
                    <div class="col-md-6 mb-3">
                      <div class="form-floating">
                        <input
                          type="text"
                          class="form-control"
                          id="floatingstreet"
                          placeholder="Địa chị cụ thể"
                        />
                        <label for="floatingstreet"
                          ><span class="text-danger">*</span> Địa chị cụ
                          thể</label
                        >
                      </div>
                    </div>
                    <!-- Street -->
                    <!-- City -->
                    <div class="col-md-6 mb-3">
                      <div class="form-floating">
                        <select class="form-select floatingcity" id="">
                          <option selected>Chọn Tỉnh/thành phố</option>
                        </select>
                        <label for="floatingcity"><span class="text-danger">*</span> Tỉnh/thành phố</label>
                      </div>
                    </div>
                    <!-- City -->
                    <!-- District -->
                    <div class="col-md-6 mb-3">
                      <div class="form-floating">
                        <select
                          name=""
                          id=""
                          class="form-select floatingdistrict"
                          disabled
                        >
                          <option selected>chọn Quận/huyện</option>
                        </select>
                        <label for="floatingdistrict"
                          ><span class="text-danger">*</span> Quận/huyện</label
                        >
                      </div>
                    </div>
                    <!-- District -->
                    <!-- Ward -->
                    <div class="col-md-6 mb-3">
                      <div class="form-floating">
                        <select
                          name=""
                          id=""
                          class="form-select floatingward"
                          disabled
                        >
                          <option selected>Chọn Xã/phường/thị trấn</option>
                        </select>
                        <label for="floatingward"
                          ><span class="text-danger">*</span> Xã/phường/thị
                          trấn</label
                        >
                      </div>
                    </div>
                    <!-- Ward -->
                    <!-- Thêm, sửa, xóa, địa chỉ -->
                    <div class="col-md-12">
                      <div
                        class="d-flex align-items-center justify-content-between mt-2"
                      >
                        <!-- bên trái set mặc định địa chỉ -->
                        <div>
                          <a href="#" class="btnDefault">
                          <i class="bi bi-star "></i>
                          </a>
                        </div>
                        <!-- bên phải thao tác đia chị -->
                        <div>
                          <a href="#" class="btn btn-success">
                            <i class="bi bi-plus-circle"></i>
                          </a>
                          <a href="#" class="btn btn-warning"
                            ><i class="bi bi-pencil-square"></i
                          ></a>
                          <a href="#" class="btn btn-danger btnDeleteAddress">
                            <i class="bi bi-trash"></i
                          ></a>
                        </div>
                        <!-- bên phải thao tác đia chị -->
                      </div>
                      <hr />
                    </div>
                    <!-- Thêm, sửa, xóa, địa chỉ --> 
                  </div>                
                  `;
      // đưa mã html mới vào DOM
      document
        .getElementById("displayAddress")
        .insertAdjacentHTML("beforeend", newAddressHtml);

      //tải dữ liệu cho trường thành phố vừa thêm
      const newSelectCity = document.querySelector(
        "#displayAddress .row:last-child .floatingcity"
      );
      loadAdress(newSelectCity);
      // đưa vào mảng
      savedAddress.push(newAddressHtml);
      localStorage.setItem("addresses", JSON.stringify(savedAddress));

      // Gọi lại hàm attachEventHandlers để gắn sự kiện cho các trường mới
      attachEventHandlers();
    });
  // xóa địa chỉ
  document
    .getElementById("displayAddress")
    .addEventListener("click", function (event) {
      // event Delegation
      if (event.target.closest(".btnDeleteAddress")) {
        // kiểm tra phần tử nào dc click
        event.preventDefault(); // ngăn chặn reload trang
        // event.target.closest(".row"); tìm phần tử cha gần nhât và lấy phần tử để xóa
        const addressRow = event.target.closest(".row");
        // tìm phần tử trong mảng
        const index = Array.from(
          document.querySelectorAll("#displayAddress .row") // trả về node list
        ).indexOf(addressRow);
        // console.log(index);

        // mảng bắt đầu từ 0
        if (index > -1) {
          savedAddress.splice(index, 1);
        }
        // cập nhật lại localStorage để hiển thị
        localStorage.setItem("addresses", JSON.stringify(savedAddress));
        addressRow.remove(); // xóa html  khỏi DOM
      }
    });
});

// load dữ liệu khi trang  được tải
const firstSelectCity = document.querySelector(".floatingcity");
loadAdress(firstSelectCity);
attachEventHandlers();
