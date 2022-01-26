import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';

const initialState = {
    status: 'idle',
    list: [],
    isEdit: false,
    editId: 0,

}


export const getBoxAsync = createAsyncThunk(
    'GET',
    async (arg, thunkAPI) => {
        const response = await fetch('http://127.0.0.1:9000/boxmanagement/boxes');
        return response;
    }
);

const boxesSlice = createSlice({
    name: 'boxes',
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
            .addCase(getBoxAsync.fulfilled, (state, action) => {
                state.status = 'idle';
                state.list = action.payload;
            });
    }

})

export const selectBoxes = (state) => state.boxes.list;

export const isEditState = (state) => state.boxes.isEdit;

export const getBox = (state,action) => state.boxes.list.filter(elem => elem.id !== action.payload.id)[0];

export const getEditBox = (state) => state.boxes.list.filter(elem => elem.id === state.boxes.editId)[0];

export const {addElement, startEditElement, editElement, cancelEdit, deleteElement} = boxesSlice.actions

export default boxesSlice.reducer