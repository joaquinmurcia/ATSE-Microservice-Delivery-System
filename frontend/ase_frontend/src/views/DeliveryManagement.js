import {Container} from "@mui/material"
import DeliveriesList from "./DeliveriesList";
import AddNewForm from "./AddNewForm";
import EditDeliveryForm from "./EditDeliveryForm";
import {makeStyles} from "@material-ui/core";

const useStyles = makeStyles(() => ({
    layout: {
        display:'flex',
        flexDirection: 'row'
    }
}));

function DeliveryManagement(){

    const { layout } = useStyles();

    const isEditing = true;

    return(
        <Container maxWidth={false}>
            <div className={layout} >
                <DeliveriesList/>
                {isEditing ? <EditDeliveryForm/> : <AddNewForm/>}


            </div>
        </Container>
    );
}

export default DeliveryManagement;