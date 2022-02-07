import { apiInstance } from "./index.js";

const api = apiInstance();

function modifyUser(user, success, fail) {
  api.put(`/user`, JSON.stringify(user)).then(success).catch(fail);
}

function registerUser(user, success, fail) {
  console.log(JSON.stringify(user));
  api.post(`/user`, JSON.stringify(user)).then(success).catch(fail);
}

function deleteUser(userId, success, fail) {
  api.delete(`/user/${userId}`).then(success).catch(fail);
}

function modifyUserPic(userId, success, fail) {
  api.post(`/user/profile/${userId}`).then(success).catch(fail);
}

function checkPassword(user, success, fail) {
  api.post(`/user/password/check`).then(success).catch(fail);
}

function changePassword(user, success, fail) {
  api.post(`/user/password/change`, JSON.stringify(user)).then(success).catch(fail);
}

function loginUser(user, success, fail) {
  api.post(`/user/login`, JSON.stringify(user)).then(success).catch(fail);
}

function userInfo(userId, success, fail) {
  api.get(`/user/${userId}`).then(success).catch(fail);
}

function findUserPassword(userEmail, success, fail) {
  api.get(`/user/password/sendToEmail/${userEmail}`).then(success).catch(fail);
}

function checkDuplication(userEmail, success, fail) {
  api.get(`/user/duplication/?email=${userEmail}`).then(success).catch(fail);
}

// 유저 즐겨찾는 병원 조회
function userFavHospital(success, fail) {
  api.get(`/mark`).then(success).catch(fail);
}

// 유저 반려동물 정보 조회
function petInfo(success, fail) {
  api.get(`/pet/list`).then(success).catch(fail);
}

// 유저 반려동물 정보 추가
function addInfoPet(success, fail) {
  api.post(`/pet`).then(success).catch(fail);
}

// 유저 반려동물 정보 수정
function changeInfoPet(petId, success, fail) {
  api.post(`/pet`).then(success).catch(fail);
}

// 유저 반려동물 정보 삭제
function deleteInfoPet(petId, success, fail) {
  api.delete(`/pet`).then(success).catch(fail);
}

export {
  userInfo,
  registerUser,
  modifyUser,
  deleteUser,
  modifyUserPic,
  checkPassword,
  changePassword,
  loginUser,
  findUserPassword,
  checkDuplication,
  userFavHospital,
  petInfo,
  addInfoPet,
  changeInfoPet,
  deleteInfoPet,
};