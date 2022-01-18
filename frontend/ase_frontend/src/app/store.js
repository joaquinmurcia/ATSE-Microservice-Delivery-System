import { configureStore } from '@reduxjs/toolkit';
import deliveriesReducer from '../views/Delivery/deliveriesSlice';
import usersReducer from '../views/User/usersSlice';

export const store = configureStore({
  reducer: {
    deliveries : deliveriesReducer,
    users : usersReducer
  },
});
