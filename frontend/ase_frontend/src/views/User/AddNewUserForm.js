import Button from "@mui/material/Button";
import {TextField, Typography, Paper, Container, MenuItem, Select, InputLabel, FormControl} from "@mui/material"
import {useDispatch} from "react-redux";
import React, {useState} from "react";
import {addElement} from "./usersSlice";


const AddNewUserForm = () => {

    const dispatch = useDispatch();

    const[elemName,setName] = useState("");
    const[elemPassword,setPassword] = useState("");
    const[elemRole,setRole] = useState("");

    const handleChangeName = (e) => {
        setName(e.target.value);
    }

    const handleChangePassword = (e) => {
        setPassword(e.target.value);
    }

    const handleChangeRole = (e) => {
        setRole(e.target.value);
    }

    function getElem() {
        const newElement = {
            name:  elemName,
            password: elemPassword,
            role: elemRole
        }

        setName("");
        setPassword("");
        setRole("")

        return newElement
    }

    return (
        <Container>
            <Paper  sx={{border: 1, borderRadius: 1}}>
                <Typography component="h1" variant="h6" align="center" marginBottom={5}>
                    Add new User
                </Typography>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="name" label="Name" value={elemName} onChange={handleChangeName}/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="password" label="Password" value={elemPassword} onChange={handleChangePassword}/>
                <br/>
                <FormControl sx={{minWidth: 120, margin: 1}} size="small">
                    <InputLabel id="selectRole">Role</InputLabel>
                    <Select name="role" labelId="selectRole" label="" value={elemRole} onChange={handleChangeRole}>
                        <MenuItem value=""><em>None</em></MenuItem>
                        <MenuItem value="customer">Customer</MenuItem>
                        <MenuItem value="deliverer">Deliverer</MenuItem>
                        <MenuItem value="dispatcher">Dispatcher</MenuItem>
                    </Select>
                </FormControl>
                <br/>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="success" onClick={()=> dispatch(addElement(getElem()))}>Add</Button>
            </Paper>
        </Container>
    );
}

export default AddNewUserForm;