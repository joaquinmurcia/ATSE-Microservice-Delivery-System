import {
    AppBar,
    Toolbar,
    Typography,
    makeStyles,
    Button,
} from "@material-ui/core";
import React from "react";
import { Link as RouterLink } from "react-router-dom";
import {useSelector} from "react-redux";
import {isLoggedIn} from "./views/loginSlice";

const headersData = [

    {
        id: "box-management",
        label: "Box Management",
        href: "/box-management",
    },
    {
        id: "delivery-management",
        label: "Delivery Management",
        href: "/delivery-management",
    },
    {
        id: "user-management",
        label: "User Management",
        href: "/user-management",
    },
    {
        id: "login",
        label: "Log In",
        href: "/login",
    },
    {
        id: "logout",
        label: "Log out",
        href: "/logout",
    },
];

const useStyles = makeStyles(() => ({
    header: {
        backgroundColor: "#400CCC",
        paddingRight: "79px",
        paddingLeft: "118px",
    },
    logo: {
        fontFamily: "Work Sans, sans-serif",
        fontWeight: 600,
        color: "#FFFEFE",
        textAlign: "left",
    },
    menuButton: {
        fontFamily: "Open Sans, sans-serif",
        fontWeight: 700,
        size: "18px",
        marginLeft: "38px",
    },
    toolbar: {
        display: "flex",
        justifyContent: "space-between",
    },
}));

export default function Header() {
    const { header, logo, menuButton, toolbar } = useStyles();

    const isLogin = useSelector(isLoggedIn);

    const displayDesktop = () => {
        return (
            <Toolbar className={toolbar}>
                {ASEDEliverLogo}
                <div>{getMenuButtons()}</div>
            </Toolbar>
        );
    };

    const ASEDEliverLogo = (
        <Typography variant="h6" component="h1" className={logo}>
            ASE Delivery
        </Typography>
    );

    const getMenuButtons = () => {
        return headersData.filter((elem)=>{

            if(isLogin){
                return elem.id !== "login";
            } else {
                return elem.id === "login";
            }
        }).map(({ id, label,  href }) => {
            return (
                <Button
                    {...{
                        key: id,
                        color: "inherit",
                        to: href,
                        component: RouterLink,
                        className: menuButton
                    }}
                >
                    {label}
                </Button>
            );
        });
    };

    return (
        <header>
            <AppBar className={header}>{displayDesktop()}</AppBar>
        </header>
    );
}