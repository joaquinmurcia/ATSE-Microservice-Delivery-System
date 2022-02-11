import React, { useState } from 'react';
import Button from "@mui/material/Button";
import {TextField, Typography, Paper, Container} from "@mui/material";
import {loginAsync} from "./loginSlice";
import {useDispatch} from "react-redux";


function Login(){

    const dispatch = useDispatch();

    const[userName,setUserName] = useState("");
    const[password,setPassword] = useState("");

    const handleUserName = (e) => {
        setUserName(e.target.value);
    }

    const handlePassword = (e) => {
        setPassword(e.target.value);
    }
    
    return (
        <Container>
            <Paper  sx={{border: 1, borderRadius: 1}}>
                <Typography component="h1" variant="h6" align="center" marginBottom={5}>
                    Login
                </Typography>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="userName" label="Username" value={userName} onChange={handleUserName}/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="password" label="Password" value={password} onChange={handlePassword}/>
                <br/>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" onClick={()=> dispatch(loginAsync({userName,password}))}>Login</Button>
            </Paper>
        </Container>);
}
export default Login;