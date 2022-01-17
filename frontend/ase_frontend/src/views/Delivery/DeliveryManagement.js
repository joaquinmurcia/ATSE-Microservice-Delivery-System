import {Container} from "@mui/material"
import DeliveriesList from "./DeliveriesList";
import AddNewDeliveryForm from "./AddNewDeliveryForm";
import EditDeliveryForm from "./EditDeliveryForm";
import {makeStyles} from "@material-ui/core";
import {useSelector} from "react-redux";
import {isEditState} from "./deliveriesSlice";

const useStyles = makeStyles(() => ({
    layout: {
        display:'flex',
        flexDirection: 'row'
    }
}));

function DeliveryManagement(){

    const { layout } = useStyles();

    const isEditing = useSelector(isEditState);

    return(
        <Container maxWidth={false}>
            <div className={layout} >
                <DeliveriesList/>
                {isEditing ? <EditDeliveryForm/> : <AddNewDeliveryForm/>}


            </div>
        </Container>
    );
}

export default DeliveryManagement;