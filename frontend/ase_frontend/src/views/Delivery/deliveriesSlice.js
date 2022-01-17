import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import list from "../../deliveryData.json";

const initialState = {
    status: 'idle',
    list: [],
    isEdit: false,
    editId: 0,

}


export const getDeliveriesAsync = createAsyncThunk(
    'bla/bla',
    async (arg, thunkAPI) => {
        const response = list ;//await fetch('localhost:9000/deliverymanagement/deliveries').then((data)=> data.json());
        return response;
    }
);

const deliveriesSlice = createSlice({
    name: 'deliveries',
    initialState,
    reducers: {
        addElement(state, action){
            action.payload.id = state.list.length +1;
            console.log(action.payload);
            state.list.push(action.payload);
        },
        startEditElement(state, action){
            state.isEdit = true;
            state.editId = action.payload.id;

        },
        editElement(state, action){
            state.list.map( elem => elem.id === action.payload.id? action.payload : elem );
            state.isEdit = false;
            state.editId = 0;
            console.log(action.payload);
        },
        cancelEdit(state){
            state.isEdit = false;
            state.editId = 0;
            console.log("Cancel")
        },
        deleteElement(state, action){
            console.log("delete" + action.payload.id);
            state.list.filter(elem => elem.id !== action.payload.id);
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(getDeliveriesAsync.fulfilled, (state, action) => {
                state.status = 'idle';
                state.list = action.payload;
            });
    }

})

export const selectDeliveries = (state) => state.deliveries.list;

export const isEditState = (state) => state.deliveries.isEdit;

export const getDelivery = (state,action) => state.deliveries.list.filter(elem => elem.id !== action.payload.id)[0];

export const getEditDelivery = (state) => state.deliveries.list.filter(elem => elem.id === state.deliveries.editId)[0];

export const {addElement, startEditElement, editElement, cancelEdit, deleteElement} = deliveriesSlice.actions

export default deliveriesSlice.reducer