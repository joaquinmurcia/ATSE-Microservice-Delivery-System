
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';

const initialState = {
    status: 'idle',
    list: [],
    isEdit: false,
    editId: 0,

}

export const getDeliveriesAsync = createAsyncThunk(
    'GET',
    async (arg, thunkAPI) => {
        const requestOptions = {
            method: "GET",
            credentials:"include"
        }
        const link = 'http://127.0.0.1:9000/deliverymanagement/deliveries';
        const response = await fetch(link,requestOptions).then((data)=> data.json());
        console.log("deliveries recieved")
        return response;
    }
);

export const deleteDeliveryAsync = createAsyncThunk(
    'DELETE',
    async(elem) => {
        const requestOptions = {
            methode: "DELETE",
            credentials: "include"
        }
        const link = '127.0.0.1:9000/deliverymanagement/deliveries/' + elem.id;
        await fetch(link ,requestOptions);
        console.log("deleted: " + elem.id)
    }
)

export const editDeliveryAsync = createAsyncThunk(
    'PUT',
    async(elem) => {
        const requestOptions = {
            methode: "PUT",
            credentials: "include",
            body: JSON.stringify(elem),
        }
        const link = 'http://127.0.0.1:9000/deliverymanagement/deliveries/' + elem.id;
        console.log(link);
        const response = await fetch(link ,requestOptions).then((data)=> data.json());
        console.log("changed " + elem.id);
        return response;
    }
)

export const addDeliveryAsync = createAsyncThunk(
    'POST',
    async(elem) => {
        const requestOptions = {
            methode: "POST",
            credentials: "include",
            body: JSON.stringify([elem]),
        }
        console.log(elem);
        const link = 'http://127.0.0.1:9000/deliverymanagement/deliveries';
        console.log(requestOptions.body);
        const response = await fetch(link ,requestOptions);
        console.log("Added new Element");
        console.log(response);
    }
)

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
            console.log("Cancel")
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
                state.list = state.list.map( elem => elem.id === action.payload.id? action.payload : elem );
                state.isEdit = false;
                state.editId = 0;
            })
            .addCase(addDeliveryAsync.fulfilled, (state, action)=> {
                state.status = 'idle';
                action.payload.id = state.list.length +1;
                state.list = state.list.push(action.payload);
            });
    }

})

export const selectDeliveries = (state) => state.deliveries.list;

export const isEditState = (state) => state.deliveries.isEdit;

export const getDelivery = (state,action) => {return state.deliveries.list.filter(elem => elem.id !== action.payload.id)[0]};

export const getEditDelivery = (state) => {return state.deliveries.list.filter(elem => elem.id === state.deliveries.editId)[0]};

export const {startEditElement, cancelEdit} = deliveriesSlice.actions;

export default deliveriesSlice.reducer