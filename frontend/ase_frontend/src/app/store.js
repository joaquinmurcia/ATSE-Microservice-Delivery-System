import { configureStore } from '@reduxjs/toolkit';
import counterReducer from '../views/counter/counterSlice';

export const store = configureStore({
  reducer: {
    counter: counterReducer,
  },
});
