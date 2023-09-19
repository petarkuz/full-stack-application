'use client'

import {
    Avatar,
    Box,
    Center,
    Flex,
    Heading,
    Image,
    Stack,
    Tag,
    TagLabel,
    Text,
    useColorModeValue
} from '@chakra-ui/react'
import DeleteCustomerButton from "./DeleteCustomerButton.jsx";
import UpdateCustomerDrawer from "./UpdateCustomerDrawer.jsx";

export default function CardWithImage({id, name, email, age, gender, imageNumber, handleDeleteCustomer, fetchCustomers}) {
    const randomUserGender = gender === "MALE" ? "men" : "women";
    const color = gender === "MALE" ? "teal" : "pink";

    return (
        <Center py={6}>
            <Box
                maxW={'300px'}
                minW={'300px'}
                m={2}
                w={'full'}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'lg'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        // 'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80'
                        'https://random.imagecdn.app/500/150'
                    }
                    objectFit="cover"
                    alt="#"
                />
                <Tag size='lg' colorScheme={color} borderRadius='full'>
                    <TagLabel>{id}</TagLabel>
                </Tag>
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={
                            `https://randomuser.me/api/portraits/${randomUserGender}/${imageNumber}.jpg`
                        }
                        css={{
                            border: `2px solid ${color}`,
                        }}
                    />
                </Flex>

                <Box p={6}>
                    <Stack spacing={2} align={'center'} mb={5}>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {name}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text color={'gray.500'}>Age: {age} | {gender}</Text>
                    </Stack>
                </Box>
                <Stack direction='row' justify={"center"} spacing={6} m={6} >
                    <UpdateCustomerDrawer
                        initialValues={{name, email, age, gender}}
                        fetchCustomers={fetchCustomers}
                        customerId={id}
                    />
                    <DeleteCustomerButton
                        handleDeleteCustomer={handleDeleteCustomer}
                        customerId={id}
                        name={name}
                    />
                </Stack>
            </Box>

        </Center>
    )
}