import './App.css';
import Header from './Header';
import UserManagement from './views/UserManagement';
import DeliveryManagement from './views/DeliveryManagement';
import BoxManagement from "./views/BoxManagement";
import {
    Routes,
    Route
} from "react-router-dom";

function App() {
    return (
        <div className="App" >
            <div className="Header"><Header/></div>
            <div className="Body">
                <Routes>
                    <Route path="box-management" element={<BoxManagement/>}/>
                    <Route path="delivery-management" element={<DeliveryManagement/>}/>
                    <Route path="user-management" element={<UserManagement/>}/>

                </Routes>
            </div>
        </div>
    );
}
export default App;