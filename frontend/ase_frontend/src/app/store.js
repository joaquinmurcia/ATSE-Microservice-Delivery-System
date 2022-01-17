import { configureStore } from '@reduxjs/toolkit';
import deliveriesReducer from '../views/Delivery/deliveriesSlice';

export const store = configureStore({
  reducer: {
    deliveries : deliveriesReducer
  },
});
