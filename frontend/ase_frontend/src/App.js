import './App.css';
import Header from './Header';
import UserManagement from './views/UserManagement';
import DeliveryManagement from './views/DeliveryManagement';
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
                    <Route path="user-management" element={<UserManagement/>}/>
                    <Route path="delivery-management" element={<DeliveryManagement/>}/>
                </Routes>
            </div>
        </div>
    );
}
export default App;