import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import {parseJwt, getCookie} from '../tokenReader';

const initialState = {
    status: 'idle',
    list: [],
    isEdit: false,
    editId: 0,

};

export const getDeliveriesAsync = createAsyncThunk(
    'GET',
    async (arg, thunkAPI) => {
        const requestOptions = {
            method: "GET",
            credentials: "include"
        };
        var link = 'http://127.0.0.1:9000/deliverymanagement/deliveries';
        const role = parseJwt(getCookie("jwt")).roles;
        const sub = parseJwt(getCookie("jwt")).sub;
        if(role === 'ROLE_CUSTOMER'){
            link = link + '?customerId=' + sub;
        } else if ( role === 'ROLE_DELIVERER'){
            link = link + '?delivererId=' + sub;
        }
        const response = await fetch(link,requestOptions).then((data)=> data.json());
        console.log("Deliveries recieved")
        return response;
    }
);

export const deleteDeliveryAsync = createAsyncThunk(
    'DELETE',
    async(elem) => {
        const requestOptions = {
            method: "DELETE",
            credentials: "include"
        };
        const link = 'http://127.0.0.1:9000/deliverymanagement/deliveries/' + elem.id;
        await fetch(link ,requestOptions);
        console.log("deleted: " + elem.id);
    }
);

export const editDeliveryAsync = createAsyncThunk(
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
        };
        const link = 'http://127.0.0.1:9000/deliverymanagement/deliveries/' + elem.id;
        const response = await fetch(link ,requestOptions).then((data)=> data.json());
        console.log("changed " + elem.id);
        return response;
    }
);

export const addDeliveryAsync = createAsyncThunk(
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
        };
        const link = 'http://127.0.0.1:9000/deliverymanagement/deliveries';
        await fetch(link ,requestOptions);
        console.log("Added new Element");
    }
);

const deliveriesSlice = createSlice({
    name: 'deliveries',
    initialState,
    reducers: {
        startEditElement(state, action){
            state.isEdit = true;
            state.editId = action.payload.id;

        },
        cancelEdit(state){
            state.isEdit = false;
            state.editId = 0;
            console.log("Cancel");
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(getDeliveriesAsync.fulfilled, (state, action) => {
                state.status = 'idle';
                state.list = action.payload;
            })
            .addCase(deleteDeliveryAsync.fulfilled, (state, action) => {
                state.status = 'idle';
            })
            .addCase(editDeliveryAsync.fulfilled, (state, action)=> {
                state.status = 'idle';
                state.isEdit = false;
                state.editId = 0;
            })
            .addCase(addDeliveryAsync.fulfilled, (state, action)=> {
                state.status = 'idle';
            });
    }

});

export const selectDeliveries = (state) => state.deliveries.list;

export const isEditState = (state) => state.deliveries.isEdit;

export const getDelivery = (state,action) => {return state.deliveries.list.filter(elem => elem.id !== action.payload.id)[0]};

export const getEditDelivery = (state) => {return state.deliveries.list.filter(elem => elem.id === state.deliveries.editId)[0]};

export const {startEditElement, cancelEdit} = deliveriesSlice.actions;

export default deliveriesSlice.reducer;