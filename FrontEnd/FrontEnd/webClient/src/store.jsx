// store.jsx
import { configureStore, combineReducers } from "@reduxjs/toolkit";
import userDataReducer from "../features/userData/userDataSlice";
import {
  persistStore,
  persistReducer,
  FLUSH,
  REHYDRATE,
  PAUSE,
  PERSIST,
  PURGE,
  REGISTER,
} from "redux-persist";
import storage from "redux-persist/lib/storage"; // Lưu vào localStorage

// Gộp các reducer
const rootReducer = combineReducers({
  userData: userDataReducer,
});

// Cấu hình persist
const persistConfig = {
  key: "root",
  storage,
};
//Neu chi muon luu user thi:
// const persistConfig = {
//   key: "userData",
//   storage,
//   whitelist: ["userData"], // chỉ lưu field này
// };


// Tạo persisted reducer
const persistedReducer = persistReducer(persistConfig, rootReducer);

// Cấu hình store
export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        // fix warning của redux-persist
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      },
    }),
});

// Tạo persistor để wrap App
export const persistor = persistStore(store);
