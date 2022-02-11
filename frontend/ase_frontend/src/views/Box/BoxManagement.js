import {Container} from "@mui/material"
import BoxList from "./BoxesList";
import AddNewBoxForm from "./AddNewBoxForm";
import EditBoxForm from "./EditBoxForm";
import {makeStyles} from "@material-ui/core";
import {useSelector} from "react-redux";
import {isEditState} from "./boxesSlice";
import {getCookie, parseJwt} from "../tokenReader";

const useStyles = makeStyles(() => ({
    layout: {
        display:'flex',
        flexDirection: 'row'
    }
}));

function BoxManagement(){

    const { layout } = useStyles();

    const isEditing = useSelector(isEditState);

    const role = parseJwt(getCookie("jwt")).roles;

    return(
        <Container maxWidth={false}>
            <div className={layout} >
                <BoxList/>
                {role === "ROLE_CUSTOMER" || role === "ROLE_DELIVERER"? <div/>: isEditing ? <EditBoxForm/> : <AddNewBoxForm/>}
            </div>
        </Container>
    );
}

export default BoxManagement;