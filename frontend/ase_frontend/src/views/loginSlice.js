import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';

const initialState = {
    status: 'idle',
    role: '',
    isLoggedIn: false,
    sub: ''
}

export const loginAsync = createAsyncThunk(
    'POST',
    async ( elem) => {
        const temp = window.btoa(elem.userName + ":" + elem.password);

        const requestOptions = {
            method: "POST",
            credentials: 'include',
            headers: {
                Authorization: "Basic " + temp
            }
        }

        console.log("Start Login")
        console.log(temp);
        const response = fetch('http://127.0.0.1:9000/usermanagement/auth', requestOptions).then(handleResponse);

        return response;
    }
);

function handleResponse(response) {
    return response.text().then(text => {
        const data = text;
        if (!response.ok) {
            console.log("Login Successful");
            const error = (data && data.message) || response.statusText;
            return Promise.reject(error);
        }
        console.log(data);
        return data;
    });
}

const loginSlice = createSlice({
    name: 'users',
    initialState,
    reducers: {
    },
    extraReducers: (builder) => {
        builder
            .addCase(loginAsync.fulfilled, (state, action) => {
                state.status = 'idle';
                state.role = parseJwt(getCookie("jwt")).roles;
                state.isLoggedIn = true;
                state.sub = parseJwt(getCookie("jwt")).sub;
            });
    }

});

function parseJwt (token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

export const isLoggedIn = (state) => state.login.isLoggedIn;

export const selectRole = (state) => state.login.role;

export const selectSub = (state) => state.login.sub;

export default loginSlice.reducer;