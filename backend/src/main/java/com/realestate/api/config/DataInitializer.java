package com.realestate.api.config;

import com.realestate.api.model.*;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final PropertyRepository propertyRepository;
        private final PasswordEncoder passwordEncoder;

        @Value("${app.seed-password:seedDev123}")
        private String seedPassword;

        @Override
        public void run(String... args) throws Exception {
                // Solo ejecutar seeding si la BD no tiene propiedades aún
                if (propertyRepository.count() > 0) {
                        log.info("BD ya inicializada, omitiendo seeding.");
                        return;
                }

                // Obtener o crear el agente semilla Nelva Torres (email dedicado, separado del
                // usuario real)
                User agent = userRepository.findByEmail("nelva@livestate.com")
                                .orElseGet(() -> {
                                        User newAgent = User.builder()
                                                        .name("Nelva Torres")
                                                        .email("nelva@livestate.com")
                                                        .password(passwordEncoder.encode(seedPassword))
                                                        .role(Role.AGENT)
                                                        .rating(4.9)
                                                        .reviewsCount(48)
                                                        .recommended(true)
                                                        .bio("Apasionada de la arquitectura contemporánea y el diseño brutalista. Nelva cuenta con más de 8 años de experiencia asesorando en las residencias más exclusivas de La Paz y el Mar de Cortés. Su filosofía se basa en crear una comunión armónica entre el espacio habitado y el majestuoso paisaje natural.")
                                                        .build();
                                        return userRepository.save(newAgent);
                                });

                // 3.1 Glam House (5 fotos)
                Property glamHouse = Property.builder()
                                .title("Glam House")
                                .description("Ven y vive una experiencia diferente, comprueba que cada espacio está diseñado para no perderte de vista la hermosa bahía y sus atardeceres 🌅")
                                .price(new BigDecimal("1200"))
                                .location("La Paz, BCS")
                                .bedrooms(4)
                                .bathrooms(5)
                                .areaSqm(450.0)
                                .type(PropertyType.HOUSE)
                                .status(PropertyStatus.AVAILABLE)
                                .agent(agent)
                                .images(new ArrayList<>())
                                .build();

                glamHouse.getImages().add(PropertyImage.builder().property(glamHouse)
                                .imageUrl("images/glam-house/ejemplo_casa1.jpg").isPrimary(true).build());
                glamHouse.getImages().add(PropertyImage.builder().property(glamHouse)
                                .imageUrl("images/glam-house/ejemplo_casa2.jpg").isPrimary(false).build());
                glamHouse.getImages().add(PropertyImage.builder().property(glamHouse)
                                .imageUrl("images/glam-house/ejemplo_casa3.jpg").isPrimary(false).build());
                glamHouse.getImages().add(PropertyImage.builder().property(glamHouse)
                                .imageUrl("images/glam-house/ejemplo_casa4.jpg").isPrimary(false).build());
                glamHouse.getImages().add(PropertyImage.builder().property(glamHouse)
                                .imageUrl("images/glam-house/ejemplo_casa5.jpg").isPrimary(false).build());

                propertyRepository.save(glamHouse);

                // 3.2 Casa Palmas (4 fotos)
                Property casaPalmas = Property.builder()
                                .title("Casa Palmas")
                                .description("En el borde del acantilado y con vistas al mar azul profundo de Cortez, esta casa de 5 dormitorios y 4 baños y medio está totalmente amueblada y muy bien equipada con todas las necesidades para tus vacaciones.")
                                .price(new BigDecimal("950"))
                                .location("La Paz, BCS")
                                .bedrooms(5)
                                .bathrooms(5)
                                .areaSqm(320.0)
                                .type(PropertyType.HOUSE)
                                .status(PropertyStatus.AVAILABLE)
                                .agent(agent)
                                .images(new ArrayList<>())
                                .build();

                casaPalmas.getImages().add(PropertyImage.builder().property(casaPalmas)
                                .imageUrl("images/palmas/ejemplo2_casa1.jpg").isPrimary(true).build());
                casaPalmas.getImages().add(PropertyImage.builder().property(casaPalmas)
                                .imageUrl("images/palmas/ejemplo2_casa2.jpg").isPrimary(false).build());
                casaPalmas.getImages().add(PropertyImage.builder().property(casaPalmas)
                                .imageUrl("images/palmas/ejemplo2_casa3.jpg").isPrimary(false).build());
                casaPalmas.getImages().add(PropertyImage.builder().property(casaPalmas)
                                .imageUrl("images/palmas/ejemplo2_casa4.jpg").isPrimary(false).build());

                propertyRepository.save(casaPalmas);

                // 3.3 Casa Chula Vista (6 fotos)
                Property casaChulaVista = Property.builder()
                                .title("Casa Chula Vista")
                                .description("¡Este encantador alojamiento está listo para darte la bienvenida a tu próxima escapada a la playa en La Paz! Perfectamente organizada para alojar a un grupo de 12 personas, esta casa está equipada con todas las comodidades de un hogar y luego algunas para garantizar que tengas una estancia cómoda cuando no estés fuera y para explorar la hermosa ciudad, impresionantes playas y actividades emocionantes.\n\n"
                                                +
                                                "En esta casa de 503 metros cuadrados | 5,414 pies cuadrados y dos niveles, encontrarás:\n\n"
                                                +
                                                "- una terraza con una piscina infinita, tumbonas, mesas de comedor y asientos acolchados que dan a un televisor inteligente montado en la pared. También hay un área donde puedes hacer barbacoa, preparar alimentos y bebidas, y un fregadero para mayor comodidad.\n\n"
                                                +
                                                "- ¡Una espaciosa planta abierta que incluye el área de estar, el comedor y la cocina con ventanas del piso al techo con vistas a la bahía de La Paz!")
                                .price(new BigDecimal("1500"))
                                .location("La Paz, BCS")
                                .bedrooms(6)
                                .bathrooms(5)
                                .areaSqm(503.0)
                                .type(PropertyType.HOUSE)
                                .status(PropertyStatus.AVAILABLE)
                                .agent(agent)
                                .images(new ArrayList<>())
                                .build();

                casaChulaVista.getImages().add(PropertyImage.builder().property(casaChulaVista)
                                .imageUrl("images/chula-vista/ejemplo3_casa1.jpg").isPrimary(true).build());
                casaChulaVista.getImages().add(PropertyImage.builder().property(casaChulaVista)
                                .imageUrl("images/chula-vista/ejemplo3_casa2.jpg").isPrimary(false).build());
                casaChulaVista.getImages().add(PropertyImage.builder().property(casaChulaVista)
                                .imageUrl("images/chula-vista/ejemplo3_casa3.jpg").isPrimary(false).build());
                casaChulaVista.getImages().add(PropertyImage.builder().property(casaChulaVista)
                                .imageUrl("images/chula-vista/ejemplo3_casa4.jpg").isPrimary(false).build());
                casaChulaVista.getImages().add(PropertyImage.builder().property(casaChulaVista)
                                .imageUrl("images/chula-vista/ejemplo3_casa5.jpg").isPrimary(false).build());
                casaChulaVista.getImages().add(PropertyImage.builder().property(casaChulaVista)
                                .imageUrl("images/chula-vista/ejemplo3_casa6.jpg").isPrimary(false).build());

                propertyRepository.save(casaChulaVista);

                log.info("BD inicializada con propiedades de ejemplo (Nelva Torres - nelva@livestate.com).");
        }
}
