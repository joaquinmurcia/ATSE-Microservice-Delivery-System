import './App.css';
import Header from './Header';
import UserManagement from './views/User/UserManagement';
import DeliveryManagement from './views/Delivery/DeliveryManagement';
import BoxManagement from "./views/BoxManagement";
import Login from "./views/Login"
import {
    Routes,
    Route,
    Navigate
} from "react-router-dom";

const isLogin = false;

function App() {
    return (
        <div className="App" >
            <div className="Header"><Header/></div>
            <div className="Body">
                <Routes>
                    <Route path="/box-management" element={<BoxManagement/>}/>
                    <Route path="/delivery-management" element={<DeliveryManagement/>}/>
                    <Route path="/user-management" element={<UserManagement/>}/>
                    <Route path="/login" element={isLogin ? <Navigate to="/" /> : <Login />}/>
                    <Route path="/" element={isLogin ? <Navigate to="/" /> : <Login />}>
                    </Route>
                </Routes>
            </div>
        </div>
    );
}
export default App;