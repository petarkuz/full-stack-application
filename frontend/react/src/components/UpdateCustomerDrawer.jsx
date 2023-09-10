import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent,
    DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    useDisclosure
} from "@chakra-ui/react";
import {CloseIcon} from "@chakra-ui/icons";
import UpdateCustomerForm from "./UpdateCustomerForm.jsx";

// const AddIcon = () => "+";

const UpdateCustomerDrawer = ({ fetchCustomers, initialValues, customerId }) => {
    const {isOpen, onOpen, onClose} = useDisclosure();

    return (
        <>
            <Button colorScheme='teal' onClick={onOpen}>
                Edit
            </Button>
            <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader>Edit customer</DrawerHeader>

                    <DrawerBody>
                        <UpdateCustomerForm
                            fetchCustomers={fetchCustomers}
                            initialValues={initialValues}
                            customerId={customerId}
                            onClose={onClose}
                        />
                    </DrawerBody>

                    <DrawerFooter>
                        <Button leftIcon={<CloseIcon/>} colorScheme='red' onClick={onClose}>
                            Close
                        </Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    )
}

export default UpdateCustomerDrawer;