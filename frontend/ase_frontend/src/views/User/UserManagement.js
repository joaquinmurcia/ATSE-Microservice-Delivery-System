import {Container} from "@mui/material"
import UserList from "./UsersList";
import AddNewUserForm from "./AddNewUserForm";
import EditUserForm from "./EditUserForm";
import {makeStyles} from "@material-ui/core";
import {useSelector} from "react-redux";
import {isEditState} from "./usersSlice";

const useStyles = makeStyles(() => ({
    layout: {
        display:'flex',
        flexDirection: 'row'
    }
}));
function UserManagement(){
    const { layout } = useStyles();

    const isEditing = useSelector(isEditState);

    return(
        <Container maxWidth={false}>
            <div className={layout} >
                <UserList/>
                {isEditing ? <EditUserForm/> : <AddNewUserForm/>}


            </div>
        </Container>
    );
}
export default UserManagement;