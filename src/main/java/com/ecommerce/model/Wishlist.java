package com.ecommerce.model;


import jakarta.persistence.*;
import lombok.*;
import org.apache.logging.log4j.util.PerformanceSensitive;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private User user;

    @ManyToOne
    private Set<Product> products = new HashSet<>();
}
