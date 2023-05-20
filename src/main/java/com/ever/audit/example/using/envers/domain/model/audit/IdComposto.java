package com.ever.audit.example.using.envers.domain.model.audit;

import com.ever.audit.example.using.envers.core.security.RevEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdComposto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rev", referencedColumnName = "rev")
    private RevEntity rev;
}
