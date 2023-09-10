import {Form, Formik} from 'formik';
import * as Yup from 'yup';
import {Button, Stack} from "@chakra-ui/react";
import {updateCustomer} from "../services/client.js";
import {errorNotification, successNotification} from "../services/notification.js";
import {MySelect, MyTextInput} from "./CreateCustomerForm.jsx";

// And now we can use these
const UpdateCustomerForm = ({fetchCustomers, initialValues, customerId, onClose}) => {
    return (
        <>
            <Formik
                initialValues={initialValues}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(36, 'Invalid name')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .min(16, 'Must be at least 16 years of age')
                        .max(99, 'Must be less than 100 years of age')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(
                            ['MALE', 'FEMALE'],
                            'Invalid gender'
                        )
                        .required('Required'),
                })}

                onSubmit={(updatedCustomer, {setSubmitting}) => {
                    setSubmitting(true);
                    updateCustomer(customerId, updatedCustomer)
                        .then(res => {
                            console.log(res);
                            successNotification(
                                "Customer updated",
                                `${updatedCustomer.name} was successfully updated.`
                            )
                            fetchCustomers();
                            onClose();
                        })
                        .catch(err => {
                            console.log(err);
                            errorNotification(
                                err.code,
                                err.response.data.message
                            )
                        }).finally(() => {
                        setSubmitting(false);
                    })
                }}
            >

                {({isValid, isSubmitting, dirty}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                                placeholder="Jane"
                            />

                            <MyTextInput
                                label="Email Address"
                                name="email"
                                type="email"
                                placeholder="jane@formik.com"
                            />

                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                                placeholder="26"
                            />

                            <MySelect label="Gender" name="gender">
                                <option value="">Select gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </MySelect>

                            <Button isDisabled={!(isValid && dirty) || isSubmitting} type="submit">Submit</Button>
                        </Stack>
                    </Form>
                )}

            </Formik>
        </>
    );
};

export default UpdateCustomerForm;