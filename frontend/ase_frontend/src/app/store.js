import { configureStore } from '@reduxjs/toolkit';
import deliveriesReducer from '../views/Delivery/deliveriesSlice';
import usersReducer from '../views/User/usersSlice';
import boxesReducer from '../views/Box/boxesSlice';

export const store = configureStore({
  reducer: {
    deliveries : deliveriesReducer,
    users : usersReducer,
    boxes: boxesReducer
  },
});
