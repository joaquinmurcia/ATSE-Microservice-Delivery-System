import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'

const initialState = {
    status: 'idle',
    list: [],

}

export const getDeliveriesAsync = createAsyncThunk(
    '',
    async (arg, thunkAPI) => {
        const response = await fetch ('../deliveryData.json').json();//await fetch('localhost:9000/deliverymanagement/deliveries').then((data)=> data.json());
        console.log(response);
        return response.deliveries; //response.content
    }
);

const deliveriesSlice = createSlice({
    name: 'deliveries',
    initialState,
    reducers: {

    },
    extraReducers: (builder) => {
        builder
            .addCase(getDeliveriesAsync.fulfilled, (state, action) => {
                state.status = 'idle';
                state.list = action.payload;
            });
    }

})

export const selectDeliveries = (state) => state.list;

export default deliveriesSlice.reducer