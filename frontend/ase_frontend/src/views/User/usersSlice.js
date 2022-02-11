import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import {parseJwt, getCookie} from '../tokenReader';

const initialState = {
    status: 'idle',
    list: [],
    isEdit: false,
    editId: 0,
}

export const getUsersAsync = createAsyncThunk(
    'GET',
    async (arg, thunkAPI) => {
        const requestOptions = {
            method: "GET",
            credentials:"include"
        }
        var link = 'http://127.0.0.1:9000/usermanagement/users';
        const role = parseJwt(getCookie("jwt")).roles;
        const sub = parseJwt(getCookie("jwt")).sub;
        if(role === 'ROLE_CUSTOMER'){
            link = link + '?customerId=' + sub;
        } else if ( role === 'ROLE_DELIVERER'){
            link = link + '?delivererId=' + sub;
        }
        const response = await fetch(link, requestOptions).then((data)=> data.json());
        console.log(response);
        return response;
    }
);

export const deleteUserAsync = createAsyncThunk(
    'DELETE',
    async(elem) => {
        const requestOptions = {
            method: "DELETE",
            credentials: "include"
        }
        const link = 'http://127.0.0.1:9000/usermanagement/users/' + elem.id;
        await fetch(link ,requestOptions);
        console.log("deleted: " + elem.id)
    }
);

export const editUserAsync = createAsyncThunk(
    'PUT',
    async(elem) => {
        const elem_json = JSON.stringify(elem);
        console.log(elem_json);
        const requestOptions = {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: "include",
            body: elem_json
        }
        const link = 'http://127.0.0.1:9000/usermanagement/users/' + elem.id;
        const response = await fetch(link ,requestOptions).then((data)=> data.json());
        console.log("changed " + elem.id);
        return response;
    }
);

export const addUserAsync = createAsyncThunk(
    'POST',
    async(elem) => {
        const elem_json = JSON.stringify([elem]);
        const requestOptions = {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: "include",
            body: elem_json,
        }
        const link = 'http://127.0.0.1:9000/usermanagement/users';
        await fetch(link ,requestOptions);
        console.log("Added new Element");
    }
);

const usersSlice = createSlice({
    name: 'users',
    initialState,
    reducers: {
        startEditElement(state, action){
            state.isEdit = true;
            state.editId = action.payload.id;

        },
        cancelEdit(state){
            state.isEdit = false;
            state.editId = 0;
            console.log("Cancel")
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(getUsersAsync.fulfilled, (state, action) => {
                state.status = 'idle';
                state.list = action.payload;
            })
            .addCase(deleteUserAsync.fulfilled, (state, action) => {
                state.status = 'idle';
            })
            .addCase(editUserAsync.fulfilled, (state, action)=> {
                state.status = 'idle';
                state.isEdit = false;
                state.editId = 0;
            })
            .addCase(addUserAsync.fulfilled, (state, action)=> {
                state.status = 'idle';
            });
    }

})

export const selectUsers = (state) => state.users.list;

export const isEditState = (state) => state.users.isEdit;

export const getUser = (state,action) => { return state.users.list.filter(elem => elem.id !== action.payload.id)[0]};

export const getEditUser = (state) => { return state.users.list.filter(elem => elem.id === state.users.editId)[0]};

export const {startEditElement, cancelEdit} = usersSlice.actions;

export default usersSlice.reducer;