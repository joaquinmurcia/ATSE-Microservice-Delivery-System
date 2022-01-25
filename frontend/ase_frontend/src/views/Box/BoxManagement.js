import {Container} from "@mui/material"
import BoxList from "./BoxesList";
import AddNewBoxForm from "./AddNewBoxForm";
import EditBoxForm from "./EditBoxForm";
import {makeStyles} from "@material-ui/core";
import {useSelector} from "react-redux";
import {isEditState} from "./boxesSlice";

const useStyles = makeStyles(() => ({
    layout: {
        display:'flex',
        flexDirection: 'row'
    }
}));

function BoxManagement(){

    const { layout } = useStyles();

    const isEditing = useSelector(isEditState);

    return(
        <Container maxWidth={false}>
            <div className={layout} >
                <BoxList/>
                {isEditing ? <EditBoxForm/> : <AddNewBoxForm/>}
            </div>
        </Container>
    );
}

export default BoxManagement;