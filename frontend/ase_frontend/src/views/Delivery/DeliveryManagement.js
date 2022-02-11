import {Container} from "@mui/material"
import DeliveriesList from "./DeliveriesList";
import AddNewDeliveryForm from "./AddNewDeliveryForm";
import EditDeliveryForm from "./EditDeliveryForm";
import {makeStyles} from "@material-ui/core";
import {useSelector} from "react-redux";
import {isEditState} from "./deliveriesSlice";
import {parseJwt, getCookie} from '../tokenReader';

const useStyles = makeStyles(() => ({
    layout: {
        display:'flex',
        flexDirection: 'row'
    }
}));

function DeliveryManagement(){

    const { layout } = useStyles();

    const isEditing = useSelector(isEditState);

    const role = parseJwt(getCookie("jwt")).roles;

    return(
        <Container maxWidth={false}>
            <div className={layout} >
                <DeliveriesList/>
                {role === "ROLE_CUSTOMER"? <div/>: isEditing ? <EditDeliveryForm/> : role === "ROLE_DELIVERER"? <div/> : <AddNewDeliveryForm/>}


            </div>
        </Container>
    );
}

export default DeliveryManagement;