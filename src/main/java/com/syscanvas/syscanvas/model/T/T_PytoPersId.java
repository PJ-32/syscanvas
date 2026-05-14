package com.syscanvas.syscanvas.model.T;

import java.io.*;
import java.util.Objects;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class T_PytoPersId implements Serializable{
    private Long codPyto;
    private Long corrEmpl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof T_PytoPersId)) return false;
        T_PytoPersId that = (T_PytoPersId) o;
        return Objects.equals(codPyto, that.codPyto) &&
            Objects.equals(corrEmpl, that.corrEmpl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codPyto, corrEmpl);
    }
}
