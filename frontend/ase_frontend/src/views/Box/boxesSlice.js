import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import {parseJwt, getCookie} from '../tokenReader';

const initialState = {
    status: 'idle',
    list: [],
    isEdit: false,
    editId: 0,

}

export const getBoxesAsync = createAsyncThunk(
    'GET',
    async (arg, thunkAPI) => {
        const requestOptions = {
            method: "GET",
            credentials:"include"
        }
        var link = 'http://127.0.0.1:9000/boxmanagement/boxes';
        const role = parseJwt(getCookie("jwt")).roles;
        const sub = parseJwt(getCookie("jwt")).sub;
        if(role === 'ROLE_CUSTOMER'){
            link = link + '?customerId=' + sub;
        } else if ( role === 'ROLE_DELIVERER'){
            link = link + '?delivererId=' + sub;
        }

        const response = await fetch(link,requestOptions).then((data)=> data.json());
        return response;
    }
);

export const deleteBoxAsync = createAsyncThunk(
    'DELETE',
    async(elem) => {
        const requestOptions = {
            method: "DELETE",
            credentials: "include"
        }
        const link = 'http://127.0.0.1:9000/boxmanagement/boxes/' + elem.id;
        await fetch(link ,requestOptions);
        console.log("deleted: " + elem.id)
    }
);

export const editBoxAsync = createAsyncThunk(
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
        const link = 'http://127.0.0.1:9000/boxmanagement/boxes/' + elem.id;
        const response = await fetch(link ,requestOptions).then((data)=> data.json());
        console.log("changed " + elem.id);
        return response;
    }
);

export const addBoxAsync = createAsyncThunk(
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
        const link = 'http://127.0.0.1:9000/boxmanagement/boxes';
        await fetch(link ,requestOptions);
        console.log("Added new Element");
    }
);

const boxesSlice = createSlice({
    name: 'boxes',
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
            .addCase(getBoxesAsync.fulfilled, (state, action) => {
                state.status = 'idle';
                state.list = action.payload;
            })
            .addCase(deleteBoxAsync.fulfilled, (state, action) => {
            state.status = 'idle';
            })
            .addCase(editBoxAsync.fulfilled, (state, action)=> {
                state.status = 'idle';
                state.isEdit = false;
                state.editId = 0;
            })
            .addCase(addBoxAsync.fulfilled, (state, action)=> {
                state.status = 'idle';
            });
    }

})

export const selectBoxes = (state) => state.boxes.list;

export const isEditState = (state) => state.boxes.isEdit;

export const getBox = (state,action) => {return state.boxes.list.filter(elem => elem.id !== action.payload.id)[0]};

export const getEditBox = (state) => {return state.boxes.list.filter(elem => elem.id === state.boxes.editId)[0]};

export const {startEditElement, cancelEdit} = boxesSlice.actions;

export default boxesSlice.reducer