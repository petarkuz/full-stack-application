'use client'

import {
    Button,
    Checkbox,
    Flex,
    Text,
    FormControl,
    FormLabel,
    Heading,
    Input,
    Stack,
    Image, Link,
} from '@chakra-ui/react'
import {Form, Formik} from "formik";
import * as Yup from 'yup';
import {MyTextInput} from "../shared/CreateCustomerForm.jsx";
import {login} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/notification.js";
import {useAuth} from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";

const LoginForm = () => {
    const {login} = useAuth();
    const navigate = useNavigate();

    return (
        <Formik validateOnMount={true}
                validationSchema={Yup.object({
                    username: Yup.string()
                        .email("Must be of type email.")
                        .required("Email is required."),
                    password: Yup.string()
                        .min(6, "Password cannot be less than 6 characters.")
                        .required("Password is required.")
                })}
                initialValues={{username: '', password: ''}}
                onSubmit={(values, {setSubmitting}) => {
                    setSubmitting(true);
                    login(values).then(res => {
                        console.log(res)
                        navigate("/dashboard");
                    }).catch(err => {
                        errorNotification(
                            err.code,
                            err.response.data.message
                        )
                    }).finally(() => {
                        setSubmitting(false);
                    })
                }}>

            {({isValid, isSubmitting}) => (//Shorter syntax for {return ()}
                <Form>
                    <Stack spacing={"24px"}>
                        <MyTextInput
                            label="Email"
                            name="username"//what is sent(Yup uses)
                            type="email"
                            placeholder="jane@formik.com"
                        />

                        <MyTextInput
                            label="Password"
                            name="password"
                            type="password"
                            placeholder="Type your password."
                        />

                        <Button isDisabled={!isValid || isSubmitting} type="submit">Login</Button>

                    </Stack>
                </Form>
            )}

        </Formik>
    )
}

const Login = () => {
    const {customer} = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (customer) {
            navigate("/dashboard");
        }
    })

    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} align={'center'} justify={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Image
                        src={"https://cdn.logo.com/hotlink-ok/logo-social.png"}
                        alt={"Logo"}
                        size={"100px"}
                    />
                    <Heading fontSize={'2xl'}>Sign in to your account</Heading>
                    <LoginForm/>
                    <Link href={"/signup"} color={"blue.500"}>Dont have an account? Please, Sign Up now.</Link>
                </Stack>
            </Flex>
            <Flex flex={1}>
                <Image
                    alt={'Login Image'}
                    objectFit={'cover'}
                    src={
                        'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                    }
                />
            </Flex>
        </Stack>
    )
}

export default Login;