import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'

const initialState = {
    status: 'idle',
    list: [],
    isEdit: false,
    editId: 0,

}


export const getUsersAsync = createAsyncThunk(
    'GET',
    async (arg, thunkAPI) => {
        const response = await fetch('http://127.0.0.1:9000/usermanagement/users');//await fetch('https://localhost:9000/deliverymanagement/deliveries').then((data)=> data.json());
        return response;
    }
);

const usersSlice = createSlice({
    name: 'users',
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
            .addCase(getUsersAsync.fulfilled, (state, action) => {
                state.status = 'idle';
                state.list = action.payload;
            });
    }

})

export const selectUsers = (state) => state.users.list;

export const isEditState = (state) => state.users.isEdit;

export const getUser = (state,action) => state.users.list.filter(elem => elem.id !== action.payload.id)[0];

export const getEditUser = (state) => state.users.list.filter(elem => elem.id === state.users.editId)[0];

export const {addElement, startEditElement, editElement, cancelEdit, deleteElement} = usersSlice.actions

export default usersSlice.reducer