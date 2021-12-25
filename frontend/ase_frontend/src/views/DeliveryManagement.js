import Button from "@mui/material/Button";
import {TextField,Typography,Paper,Container} from "@mui/material"
import DeliveriesList from "./DeliveriesList";
import AddNewForm from "./AddNewForm";
import {makeStyles} from "@material-ui/core";

const useStyles = makeStyles(() => ({
    layout: {
        display:'flex',
        flexDirection: 'row'
    }
}));

function DeliveryManagement(){

    const { layout } = useStyles();

    return(
        <Container >
            <div className={layout}>
                <DeliveriesList/>
                <AddNewForm/>
            </div>
        </Container>
    );
}

export default DeliveryManagement;