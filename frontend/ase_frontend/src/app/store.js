import { configureStore } from '@reduxjs/toolkit';
import deliveriesReducer from '../views/deliveriesSlice';

export const store = configureStore({
  reducer: {
    deliveriesReducer
  },
});
